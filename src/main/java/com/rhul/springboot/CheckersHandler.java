package com.rhul.springboot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONException;//should be fixed

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.json.JSONObject;

import java.util.concurrent.locks.ReentrantLock;

/**The main handler class that's responsible for communicating with the client and triggering server command.
 * @author Azky
 */

public class CheckersHandler extends TextWebSocketHandler {


    private static final String game_attribute = "checkers";
    private static final String player_white = "White";
    private static final String player_black = "black";
    private ReentrantLock Lk = new ReentrantLock();
    private WebSocketSession s;
    private Checkers checks_obj;
    private boolean joining =false;
    CheckersGame game = new CheckersGame();
    Executor executor = Executors.newFixedThreadPool(20);
    String cur_plyr = null;
    int piece_index = 0;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {

            String payload = message.getPayload();
            JSONObject json = new JSONObject(payload);
            String type = json.getString("type");
            System.out.println("------------------------------------");
            System.out.println("string from f/e "+ json);


            switch (type){


                case "user":

                    Runnable threads_area = () -> {
                          try{
                              String msg;
                              System.out.println("waiting for lock");
                              Lk.lock();
                              System.out.println("I am in");

                              if (game.player_ids.get()> 4){
                                  game.player_ids.set(1);
                              }

                              int player_id = game.player_ids.getAndIncrement();
                              Player plyr = new Player(player_id,"player",session);
                              plyr.setCur_thread(Thread.currentThread());
                              session.getAttributes().put(game_attribute,plyr);

                              Room rm;
                              String rm_val = json.getString("room_value");
                              System.out.println("multi threads running n: " + Thread.currentThread().getId() + " playerId: "+ player_id);

                             if (json.getString("room_action").equals("create_room")) {


                                 if (!game.check_room_exists(rm_val)) {

                                     rm = new Room(player_id, rm_val, plyr);
                                     rm.add_player_to_room(plyr);
                                     plyr.setRoom(rm);
                                     plyr.setColour("white");
                                     Player.players_hm.put(player_id, plyr);

                                     if (rm.getSmphore().availablePermits()+1==4){
                                         plyr.setRoom_value(rm_val);
                                         plyr.start_game_thread();
                                     }
                                     game.add_rooms(rm);
                                     game.add_player(plyr);
                                     int room_permits = rm.getSmphore().availablePermits();
                                     msg = String.format("{\"type\": \"create_room_resp\",\"data\":\"Ok\",\"player_id\":\"%d\"}",player_id);
                                     plyr.sendMessage(msg);
                                     System.out.println("Create room user done");


                                 } else {

                                     msg = "{\"type\": \"create_room_resp\",\"data\":\"already_exists\"}";
                                     plyr.sendMessage(msg);

                                     return;
                                 }
                                 Lk.unlock();
                                 System.out.println("create room lock released");

                             }

                             else if ((json.getString("room_action").equals("join_room"))) {

                                 System.out.println("104");
                                 if (game.check_room_exists(rm_val)) {

                                     System.out.println("106");
                                     rm = game.get_room(rm_val);
                                     boolean player_added = rm.add_player_to_room(plyr);
                                     joining = true;
                                     game.add_player(plyr);

                                     int semaphore_permits = rm.getSmphore().availablePermits();
                                     plyr.setRoom_value(rm_val);
                                     Player.players_hm.put(player_id, plyr);

                                     if (semaphore_permits + 1 == 3 || semaphore_permits + 1 == 1 ){

                                         CheckersGame.player_game_hm.put(plyr,plyr.initialize());
                                         plyr.setColour("black");
                                         plyr.setRoom_value(rm_val);
                                         plyr.start_game_thread();
                                         System.out.println(" game 1/2 opponent ready 120");
                                     }
                                     else if(semaphore_permits + 1 == 2){
                                         plyr.setRoom_value(rm_val);
                                         plyr.setColour("white");
                                         plyr.start_game_thread();
                                     }
                                     else if (semaphore_permits == 0) {
                                         msg = "{\"type\": \"join_room_resp\",\"data\":\"game_full\"}";
                                         plyr.sendMessage(msg);
                                         rm.setGame_started(true);
                                     }

                                     if (semaphore_permits > 0 & (!rm.isGame_started())) {

                                         rm.setGame_started(true);
                                         System.out.println("136 joining player rdy");
                                     }

                                     if (player_added && player_id < 3) {
                                         msg = String.format("{\"type\": \"player_joined\",\"data\":\"successful\",\"player_id\":\"%d\"}",player_id);
                                         plyr.setRoom(rm);
                                         plyr.sendMessage(msg);
                                     }
                                     else if (player_added && player_id > 2){
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
                                 System.out.println("join room lk released");
                                 }

                          } catch (Exception ex) {
                              ex.printStackTrace();
                              BugsnagConfig.bugsnag().notify(new RuntimeException("Error encountered in joinig room"));
                          }
                    };
                  executor.execute(threads_area);
                  break;
                case "show_moves":
                    Player playre = get_player_obj(json.getInt("player_id"));
                    System.out.println("Current player id"+ playre.getId());
                    int index = json.getInt("index");
                    playre.setIndex(index);
                    playre.setShow_moves_req(true);
                    break;

                case "make_move":
                    Player plyr = get_player_obj(json.getInt("player_id"));
                    plyr.setSqr_index(json.getInt("sqr_index"));
                    plyr.setMove_req(true);
                    break;

                case "start_game":
                    plyr = get_player_obj(json.getInt("player_id"));
                    plyr.getRoom().setGame_started(true);
                    Room rm = game.get_room(json.getString("room_value"));
                    String msg = "{\"type\": \"start_game_1\",\"data\": \"rdy\"";
                    rm.apply_to_room_users(msg,rm,plyr);
                    break;


                case "start_game2":
                    plyr = get_player_obj(json.getInt("player_id"));
                    plyr.getRoom().setGame_started(true);
                    rm = game.get_room(json.getString("room_value"));
                    msg = "{\"type\": \"start_game_2\",\"data\": \"rdy_2\"";
                    rm.apply_to_room_users(msg,rm,plyr);
                    break;


                case "get_room_permits":
                    Player p = (Player) session.getAttributes().get(game_attribute);
                    System.out.println("Player " + p.getId());
                    Room rom = game.get_room(json.getString("room_value"));
                    int room_permits = rom.getSmphore().availablePermits() + 1;
                    String mesg = String.format("{\"type\": \"room_permits\",\"data\": \"%d\"}", room_permits);
                    System.out.println(mesg);
                    p.sendMessage(mesg);
                    break;


                case "get_room_players":
                    if (game.player_ids.get() > 1){ //not shows if its the first player in the game
//                        Player player_obj = (Player) session.getAttributes().get(game_attribute);
                        System.out.println("Player obj " + session);//try to use session
                        game.get_rooms_data(session);

                    }
                    break;

                case "enter_chat_lobby":
                    int plyr_id = game.lobby_plyrs.getAndIncrement();
                    Player player_instance = new Player(plyr_id,"player",session);
                    CheckersGame.lobby_chat_players.put(plyr_id,player_instance);
                    break;

                case "global_chat":
                    String global_msg = "{\"type\": \"chat\",\"msg\": \"" + json.getString("msg") + "\"}";
                    game.global_broadcast(global_msg);
                    break;
                case "game_chat":
                    String usr_msg = "{\"type\": \"chat\",\"msg\": \"" + json.getString("msg") + "\"}";
                    game.game_broadcast(usr_msg);
                    break;

                case "connection_incoming":
                    System.out.print("connection active");
                    break;

                case "adjust_screen_size":
                    int move_length = json.getInt("move_length");
                    int move_dev = json.getInt("move_dev");

                    Checkers.move_length = move_length;
                    Checkers.move_deviation = move_dev;
                    break;

                case "ping":

                    break;

                case "game_finish":
                    p = (Player) session.getAttributes().get(game_attribute);
                    System.out.println("Player value " + p.getId());
                    mesg = String.format("{\"type\": \"game_finish_resp\",\"data\": \"game_terminated\"}");
                    p.sendMessage(mesg);
                    System.out.println("Game finished, thanks for playing");
                    break;
                case "update_player_id":
                    game.player_ids.set(1);
                    break;
            }

        } catch (Exception e){
            System.err.println("Exception processing message: " + message.getPayload());
            BugsnagConfig.bugsnag().notify(new RuntimeException("F/e message cannot be processed/resovled error"));
            e.printStackTrace(System.err);
        }

    }

    public Player get_player_obj(int id){


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
