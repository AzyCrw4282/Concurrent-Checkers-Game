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
    private Player plyr;



    //handles All Messages Received From f/e Customers
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            int player_id =game.player_ids.getAndIncrement();;
            String payload = message.getPayload();
            JSONObject json = new JSONObject(payload);
            String type = json.getString("type");//this could be of any


            switch (type){

                //a user_action var passed from js handels all in the switch statement
                case "user"://all user related calls will be passed here. other cases would include rooms, chat, user handling etc.

                    Runnable threads_area = () -> {
                        System.out.println("multi threads running n: " + Thread.currentThread().getId() + "playerId: "+ player_id);//shows b/e threads running
                          try{

                              String msg;
                              String mesg;
                              Lk.lock();//sets a lock. specify to unlock at any necessary position
//                            int difficulty = json.getInt("difficulty");
                              plyr = new Player(player_id,"player",session);
                              plyr.setCur_thread(Thread.currentThread());
                              session.getAttributes().put(game_attribute,plyr);//session can manage the right object and can retrieve it
                              System.out.println("Sesssion atts set for the player");
                              String user_action = json.getString("user_action");
                              Room rm;//to  be initized
                              String rm_val = json.getString("room_value");

                             if (json.getString("room_action").equals("create_room")) {
                                 //if create rm check if exists-> false then wrong request
                                 if (!game.check_room_exists(rm_val)) {
                                     //print val, create room obj, add plyr, game add the room,
                                     rm = new Room(player_id, rm_val, plyr);
                                     rm.add_player_to_room(plyr);
                                     plyr.setRoom(rm);

                                     if (rm.getSmphore().availablePermits()+1==4){//permits held by user
                                         plyr
                                         plyr.start_game_thread();
                                     }


                                     game.add_rooms(rm);
                                     game.add_player(plyr);
                                     int room_permits = rm.getSmphore().availablePermits();
                                     msg = "{\"type\": \"create_room_resp\",\"data\":\"Ok\"}";
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
                                 if (game.check_room_exists(rm_val)) {

                                     //rm exists
                                     //set the room and get it from game class, add the player,

                                     rm = game.get_room(rm_val);
                                     boolean player_added = rm.add_player_to_room(plyr);
                                     joining = true;
                                     game.add_player(plyr);

                                     int semaphore_permits = rm.getSmphore().availablePermits();


                                     if (semaphore_permits + 1 == 3){
                                         //it can then be initialized.
                                     }


                                     if (semaphore_permits == 0) {
                                         msg = "{\"type\": \"join_room_resp\",\"data\":\"game_full\"}";
                                         plyr.sendMessage(msg);
                                         rm.setGame_started(true);//ensure to update that game is full
                                     } else if (semaphore_permits > 0 & (!rm.isGame_started())) {
                                         msg = "{\"type\": \"join_room_resp\",\"data\":\"rdy_to_join\"}";
                                         rm.getRm_owner().sendMessage(msg);//send start instruction to game owner
//                                         plyr.sendMessage(msg);
                                         rm.setGame_started(true);
                                     }
                                     //player handling
                                     if (player_added) {
                                         msg = "{\"type\": \"player_joined\",\"data\":\"successful\"}";
                                         plyr.setRoom(rm);
                                         plyr.sendMessage(msg);
                                     } else {
                                         msg = "{\"type\": \"player_joined\",\"data\":\"not_successful\"}";
                                         plyr.sendMessage(msg);
                                     }

                                 } else {
                                     msg = "{\"type\": \"join_room_resp\",\"data\":\"not_successful\"}";
                                     plyr.sendMessage(msg);
                                    }
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

                    //At this point, 2 players in
//                    Room rm = game.get_room(json.getString("room_value"));
//                    String str_player_id = json.getString("index");//index or id val of the piece
//                    piece_index = Integer.parseInt(str_player_id);//all vals used for this case and move cases
//                    String playr_colour = json.getString("player_colour");
//                    System.out.println("129");
//                    Player plyr = (Player) session.getAttributes().get(game_attribute);
//                    //To check for possible moves for a given piece
//
//                    if (playr_colour.equals("white")) {
//                        cur_plyr = "white";
//                        if (checks_obj.show_moves(checks_obj.w_checkers[piece_index], rm)) {//if an attack/move possible
//                            String mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
//                            plyr.sendMessage(mesg);
//
//                        }
//                    } else if (checks_obj.show_moves(checks_obj.b_checkers[piece_index], rm)) {
//                        System.out.println("black player");
//                        cur_plyr = "black";
//                        String mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
//                        plyr.sendMessage(mesg);
//                    }
//                    break;

                case "make_move":

//                    rm = game.get_room(json.getString("room_value"));
//                    int square_index = json.getInt("index");
//                    System.out.println("make_move_b/e  call");
//                    plyr = (Player) session.getAttributes().get(game_attribute);
//
//                    if (cur_plyr.equals("white")) {
//                        if (checks_obj.make_move(square_index, cur_plyr, rm)) {//if an attack/move possible
//                            String mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
//                            plyr.sendMessage(mesg);
//                        }
//                    } else if (checks_obj.make_move(square_index, cur_plyr, rm)) {
//                        cur_plyr = "black";
//                        String mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
//                        plyr.sendMessage(mesg);
//                    }
//                    break;

                case "start_game"://so before the 4 threshold is reached/ the user has pressed the btn
                    //f/e update for anyone in rm that game is rdy
                    plyr = (Player) session.getAttributes().get(game_attribute);
                    plyr.getRoom().setGame_started(true);//so the first player, e.g room holder
                    //get all users and send them a msg that game is rdy and update boolean var
                    Room rm = game.get_room(json.getString("room_value"));
                    String msg = "{\"type\": \"start_game\",\"data\": \"rdy\"}";
                    //the checker value to begin with on all ends.
                    rm.apply_to_room_users(msg,rm);//should not be for all
                    break;

                case "get_room_permits":
                    Player p = (Player) session.getAttributes().get(game_attribute);
                    System.out.println("plasdf " + p + "asd " + plyr);
                    Room rom = game.get_room(json.getString("room_value"));
                    int room_permits = rom.getSmphore().availablePermits() +1;//permit held by the user
                    String mesg = String.format("{\"type\": \"room_permits\",\"data\": \"%d\"}", room_permits);
                    plyr.sendMessage(mesg);
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
                    System.out.println("screen size adjusted");
                    break;

                case "sync_data":
                    //To sync f/e and b/e
                    break;

                //other cases for chat, room handling to be written

            }

        } catch (Exception e){
            System.err.println("Exception processing message: " + message.getPayload());
            e.printStackTrace(System.err);
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {





    }


}
