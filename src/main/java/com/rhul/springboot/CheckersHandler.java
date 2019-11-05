package com.rhul.springboot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.util.JSONPObject;
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
    private static final String game_att = "checkers";
    private static final String player_white = "White";
    private static final String player_black = "black";
    private ReentrantLock Lk = new ReentrantLock();
    private WebSocketSession s;
    private Checkers checks_obj;

    CheckersGame game = new CheckersGame();
    Executor executor = Executors.newFixedThreadPool(10);//
    String cur_plyr = null;
    int piece_index = 0;


    //handles All Messages Received From f/e Customers
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {

            int player_ids = game.player_ids.getAndIncrement();
            String payload = message.getPayload();
            JSONObject json = new JSONObject(payload);

            String type = json.getString("type");//this could be of any
            switch (type){

                //a user_action var passed from js handels all in the switch statement
                case "user"://all user related calls will be passed here. other cases would include rooms, chat, user handling etc.

                    Runnable threads_area = () -> {
                          try{
                              String mesg;
                              Lk.lock();//sets a lock. specify to unlock at any necessary position
//                              String name = json.getString("name");
//                              int difficulty = json.getInt("difficulty");
//                              String game_type = json.getString("game_type");//to construct diff game type

                              Player player = new Player(player_ids,"player",session);
                              player.setCur_thread(Thread.currentThread());
                              session.getAttributes().put(game_att,player);//session can manage the right object and can retrieve it

                              if (json.getString("user_action").equals("initialize")){
                                  System.out.println("game initializer");
                                  //used as parent/main objects
                                  CheckersSquare checks_sqr = new CheckersSquare();
                                  checks_obj = new Checkers(player,checks_sqr);


                                  //To fully initialize the game
                                  //id and session field act as unique in this case

                                  for (int i = 1; i <=64; i++){
                                      checks_sqr.block[i] = new CheckersSquare(i);//64 objects of squares

                                  }
                                  // white counters
                                  for (int i = 1; i <= 4; i++){
                                      checks_obj.w_checkers[i] = new Checkers(i, "white", 2*i -1 );
                                      checks_obj.w_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[2*i - 1].setOccupied();
                                      checks_sqr.block[2*i - 1].setPieceId(checks_obj.w_checkers[i]);
                                  }

                                  for (int i = 5; i <= 8; i++){
                                      checks_obj.w_checkers[i] = new Checkers(i, "white", 2*i );
                                      checks_obj.w_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[2*i].setOccupied();
                                      checks_sqr.block[2*i].setPieceId(checks_obj.w_checkers[i]);
                                  }

                                  for (int i = 9; i <= 12; i++){
                                      checks_obj.w_checkers[i] = new Checkers(i, "white", 2*i - 1 );
                                      checks_obj.w_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[2*i - 1].setOccupied();
                                      checks_sqr.block[2*i - 1].setPieceId(checks_obj.w_checkers[i]);
                                  }


                                  for (int i = 1; i <= 4; i++){
                                      checks_obj.b_checkers[i] = new Checkers(i, "black", 56 + 2*i  );
                                      checks_obj.b_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[56 +  2*i ].setOccupied();
                                      checks_sqr.block[56 +  2*i ].setPieceId(checks_obj.b_checkers[i]);
                                  }

                                  for (int i = 5; i <= 8; i++){
                                      checks_obj.b_checkers[i] = new Checkers(i, "black", 40 +  2*i - 1 );
                                      checks_obj.b_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[ 40 + 2*i - 1].setOccupied();
                                      checks_sqr.block[ 40 + 2*i - 1].setPieceId(checks_obj.b_checkers[i]);
                                  }

                                  for (int i = 9; i <= 12; i++){
                                      checks_obj.b_checkers[i] = new Checkers(i, "black", 24 + 2*i  );
                                      checks_obj.b_checkers[i].setCoordinates(0,0);
                                      checks_sqr.block[24 + 2*i ].setOccupied();
                                      checks_sqr.block[24 + 2*i ].setPieceId(checks_obj.b_checkers[i]);
                                  }

                              }

                              else if (json.getString("user_action").equals("show_moves")){
                                  String str_player_id = json.getString("index");//index or id val of the piece
                                  piece_index = Integer.parseInt(str_player_id);//all vals used for this case and move cases
                                  String playr_colour = json.getString("player_colour");
                                  System.out.println("129");
                                  //To check for possible moves for a given piece

                                  if (playr_colour.equals("white")){
                                      cur_plyr = "white";
                                      if (checks_obj.w_checkers[piece_index].show_moves(checks_obj.w_checkers[piece_index],player)){//if an attack/move possible
                                          mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
                                          player.sendMessage(mesg);

                                      }
                                  }
                                  else if(checks_obj.b_checkers[piece_index].show_moves(checks_obj.b_checkers[piece_index],player)){
                                      System.out.println("black pl;ayer");
                                      cur_plyr = "black";
                                      mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
                                      player.sendMessage(mesg);
                                  }
                                Lk.unlock();

                              }
                              else if (json.getString("user_action").equals("make_move")){

                                  int square_index = json.getInt("index");

                                  if (cur_plyr.equals("white")){
                                      if (checks_obj.w_checkers[piece_index].make_move(square_index,cur_plyr,player)){//if an attack/move possible
                                          mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
                                          player.sendMessage(mesg);
                                      }
                                  }
                                  else if(checks_obj.b_checkers[piece_index].make_move(square_index,cur_plyr,player)){
                                      cur_plyr = "black";
                                      mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
                                      player.sendMessage(mesg);
                                  }

                                  Lk.unlock();
                              }
                              else if (json.getString("user_action").equals("match_making")){
                                  Lk.unlock();
                              }
                              else if (json.getString("user_action").equals("chat")){

                              }
                          }
                          catch (Exception e){
                              e.printStackTrace();
                              //error reporting feature can be used here maybe..........
                          }

                    };
                  executor.execute(threads_area);
                  break;
                case "initialize_game":
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
            System.err.println("Exception processing message " + message.getPayload());
            e.printStackTrace(System.err);
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }








}
