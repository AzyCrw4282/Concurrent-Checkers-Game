package com.rhul.springboot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.json.JSONObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/*
The main handler class and responsible for all socket communications.
 */
public class CheckersHandler extends TextWebSocketHandler {

    //Object creations and initialization will have to be done
    //within the thread in most cases.
    private static final String game_attribute = "checkers";
    private static final String player_white = "White";
    private static final String player_black = "black";
    private ReentrantLock Lk = new ReentrantLock();
    private WebSocketSession s;
    private Checkers checks_obj;
    private boolean joining =false;
    CheckersGame game = new CheckersGame();//only one game instance bt with many rooms and also players in or in the given rooms
    Executor executor = Executors.newFixedThreadPool(10);//
    String cur_plyr = null;
    int piece_index = 0;



    //handles All Messages Received From f/e Customers
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {

            String payload = message.getPayload();
            JSONObject json = new JSONObject(payload);
            String type = json.getString("type");//this could be of any
            System.out.println("------------------------------------");
            System.out.println("string from f/e "+ json);

            switch (type){

                //a user_action var passed from js handels all in the switch statement
                case "user"://all user related calls will be passed here. other cases would include rooms, chat, user handling etc.

                    Runnable threads_area = () -> {
                          try{

                              String msg;
                              System.out.println("waiting for lock");
                              Lk.lock();//sets a lock. specify to unlock at any necessary position
                              System.out.println("I am in");
//                            int difficulty = json.getInt("difficulty");
                              System.out.println("Player_id_to_be_Set");
                              int player_id = game.player_ids.getAndIncrement();
                              Player plyr = new Player(player_id,"player",session);
                              plyr.setCur_thread(Thread.currentThread());
                              session.getAttributes().put(game_attribute,plyr);//session can manage the right object and can retrieve it
                              String user_action = json.getString("user_action");
                              Room rm;//to  be initized
                              String rm_val = json.getString("room_value");
                              System.out.println("multi threads running n: " + Thread.currentThread().getId() + " playerId: "+ player_id);//shows b/e threads running

                             if (json.getString("room_action").equals("create_room")) {
                                 //if create rm check if exists-> false then wrong request
                                 if (!game.check_room_exists(rm_val)) {
                                     //print val, create room obj, add plyr, game add the room,
                                     rm = new Room(player_id, rm_val, plyr);
                                     rm.add_player_to_room(plyr);
                                     plyr.setRoom(rm);
                                     plyr.setColour("black");//starting player black
                                     Player.players_hm.put(player_id,plyr);//static h_m that sets the vals of plyrs

                                     if (rm.getSmphore().availablePermits()+1==4){//permit held by user
                                         plyr.setRoom_value(rm_val);
                                         plyr.start_game_thread();
                                     }
                                     game.add_rooms(rm);
                                     game.add_player(plyr);
                                     int room_permits = rm.getSmphore().availablePermits();
                                     msg = String.format("{\"type\": \"create_room_resp\",\"data\":\"Ok\",\"player_id\":\"%d\"}",player_id);
                                     plyr.sendMessage(msg);


                                 } else {
                                     //already exist wrong request
                                     msg = "{\"type\": \"create_room_resp\",\"data\":\"already_exists\"}";
                                     plyr.sendMessage(msg);
                                     // snakeGame.unlock();
                                     return;
                                 }
                                 Lk.unlock();

                             }

                             else if ((json.getString("room_action").equals("join_room"))) {

                                 System.out.println("104");
                                 if (game.check_room_exists(rm_val)) {
                                     // rm exists , set the room and get it from game class, add the player,
                                     System.out.println("106");
                                     rm = game.get_room(rm_val);
                                     boolean player_added = rm.add_player_to_room(plyr);
                                     joining = true;
                                     game.add_player(plyr);

                                     int semaphore_permits = rm.getSmphore().availablePermits();
                                     plyr.setRoom_value(rm_val);
                                     Player.players_hm.put(player_id,plyr);//static h_m that sets the vals of plyrs

                                     if (semaphore_permits + 1 == 3 || semaphore_permits + 1 == 1 ){//3rd player or last player can initized the game
                                         //it can then be initialized. playing player of game and room owner can be fetched as needed.
                                         CheckersGame.player_game_hm.put(plyr,plyr.initialize());
                                         plyr.setColour("white");
                                         plyr.setRoom_value(rm_val);
                                         plyr.start_game_thread();
                                         System.out.println(" game 1/2 opponent ready 120");
                                     }
                                     else if(semaphore_permits + 1 == 2){
                                         plyr.setRoom_value(rm_val);
                                         plyr.setColour("black");
                                         plyr.start_game_thread();
                                     }
                                     else if (semaphore_permits == 0) {
                                         msg = "{\"type\": \"join_room_resp\",\"data\":\"game_full\"}";
                                         plyr.sendMessage(msg);
                                         rm.setGame_started(true);//ensure to update that game is full
                                     }

                                     if (semaphore_permits > 0 & (!rm.isGame_started())) {
                                         msg = "{\"type\": \"join_room_resp\",\"data\":\"rdy_to_join\"}";
                                         rm.getRm_owner().sendMessage(msg);//send start instruction to game owner
//                                         plyr.sendMessage(msg);
                                         rm.setGame_started(true);
                                         System.out.println("136 joining player rdy");
                                     }
                                     //player handling
                                     if (player_added && player_id < 3) {
                                         msg = String.format("{\"type\": \"player_joined\",\"data\":\"successful\",\"player_id\":\"%d\"}",player_id);
                                         plyr.setRoom(rm);
                                         plyr.sendMessage(msg);
                                     }
                                     else  if (player_added && player_id > 2){//2nd game last player
                                         System.out.println("last player added 149");
                                         msg = String.format("{\"type\": \"player_joined2\",\"data\":\"successful\",\"player_id\":\"%d\"}",player_id);
                                         plyr.setRoom(rm);
                                         plyr.sendMessage(msg);
                                     }
                                     else {
                                         msg = "{\"type\": \"player_joined\",\"data\":\"not_successful\"}";
                                         plyr.sendMessage(msg);
                                     }

                                 } else {
                                     msg = "{\"type\": \"join_room_resp\",\"data\":\"not_successful\"}";
                                     plyr.sendMessage(msg);
                                    }
                                 Lk.unlock();
                                 System.out.println("join room lk relaesed");
                                 }


                             if (json.getString("user_action").equals("initialize") & !joining) {

                                 //set the game started process here
//                                 Lk.unlock(); Does nt require unlock since it will be handled in another if block
                             }

                          } catch (Exception ex) {
                              ex.printStackTrace();
                          }

                    };
                  executor.execute(threads_area);
                  break;
                case "show_moves":
                    Player plyr = get_player_obj(json.getInt("player_id"));
                    System.out.println("Current player id"+ plyr.getId());
                    int index = json.getInt("index");
                    //need to get the right player obj
                    plyr.setIndex(index);
                    plyr.setShow_moves_req(true);
                    break;

                case "make_move":
                    plyr = get_player_obj(json.getInt("player_id"));
                    plyr.setSqr_index(json.getInt("sqr_index"));
                    plyr.setMove_req(true);
                    break;

                case "start_game"://so before the 4 threshold is reached/ the user has pressed the btn
                    //f/e update for anyone in rm that game is rdy
//                    plyr = (Player) session.getAttributes().get(game_attribute);
                    plyr = get_player_obj(json.getInt("player_id"));
                    plyr.getRoom().setGame_started(true);//so the first player, e.g room holder
                    //get all users and send them a msg that game is rdy and update boolean var
                    Room rm = game.get_room(json.getString("room_value"));
                    String msg = "{\"type\": \"start_game_1\",\"data\": \"rdy\"}";
                    //the checker value to begin with on all ends.
                    rm.apply_to_room_users(msg,rm,plyr);//should not be for all
                    break;

                //need another case for game 2
                case "start_game2"://so before the 4 threshold is reached/ the user has pressed the btn
                    //f/e update for anyone in rm that game is rdy
//                    plyr = (Player) session.getAttributes().get(game_attribute);
                    plyr = get_player_obj(json.getInt("player_id"));
                    plyr.getRoom().setGame_started(true);//so the first player, e.g room holder
                    //get all users and send them a msg that game is rdy and update boolean var
                    rm = game.get_room(json.getString("room_value"));
                    msg = "{\"type\": \"start_game_2\",\"data\": \"rdy_2\"}";
                    //the checker value to begin with on all ends.
                    rm.apply_to_room_users(msg,rm,plyr);//should not be for all
                    break;

                case "get_room_permits":
                    Player p = (Player) session.getAttributes().get(game_attribute);
                    System.out.println("Player " + p.getId());//tell if it's unique
                    Room rom = game.get_room(json.getString("room_value"));
                    int room_permits = rom.getSmphore().availablePermits() +1;//permit held by the user
                    String mesg = String.format("{\"type\": \"room_permits\",\"data\": \"%d\"}", room_permits);
                    System.out.println(mesg);
                    p.sendMessage(mesg);
                    break;

                case "connection_incoming":
                    System.out.print("connection active");
                    break;

                case "adjust_screen_size":
                    int move_length = json.getInt("move_length");
                    int move_dev = json.getInt("move_dev");
                    //static changes made since >1 game will be of same size.
                    Checkers.move_length = move_length;
                    Checkers.move_deviation = move_dev;
                    break;
                case "ping":
//                    System.out.println();//keeps conenction alive
                //other cases for chat, room handling to be written

            }

        } catch (Exception e){
            System.err.println("Exception processing message: " + message.getPayload());
            e.printStackTrace(System.err);
        }

    }

    public Player get_player_obj(int id){

        //iterate through hm and check for the correct val of id
        for (Player p : Player.players_hm.values()){
            if (p.getId() == id){
                return p;
            }
        }
        return null;

    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {





    }


}
