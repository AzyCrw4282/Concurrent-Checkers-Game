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

    private static final String player_white = "White";
    private static final String player_black = "black";
    private ReentrantLock Lk = new ReentrantLock();
    private Player player_obj = new Player();
    Executor executor = Executors.newFixedThreadPool(10);



    //handles All Messages Received From f/e Customers
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            int id = player_obj.playerId.getAndIncrement();
            String payload = message.getPayload();
            JSONObject json = new JSONObject(payload);
            CheckersGame checkers_obj;
            CheckersSquare cSquare_obj;

//            Checkers[] w_checker = new Checkers[12];
//            Checkers[] b_checker = new Checkers[12];

            switch (type){
                case "user"://all user ineractions listed in this case
                    String name = json.getString("name");
                    int difficulty = json.getInt("difficulty");
                    String game_type = json.getString("game_type");//to construct diff game type


                    checkers_obj = new CheckersGame(id,session,name);

                    String type = json.getString("type");//this could be of any
                    //to initialize or any incoming msg from the backend

                    //request to create the game
                        //if room does not exist



                        //if room exists




                    //join intent

                case "initialize_game":
                    //To fully initialize the game
                    //id and session field act as unique in this case

                    for (int i = 1; i <=64; i++){
                        CheckersSquare.block[i] = new CheckersSquare(i);//64 objects of squares
                    }

                    // white Ladies
                    for (int i = 1; i <= 4; i++){
                        Checkers.w_checkers[i] = new Checkers(i, "white", 2*i -1 );
                        Checkers.w_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[2*i - 1].setOccupied();
                        CheckersSquare.block[2*i - 1].setPieceId(Checkers.w_checkers[i]);
                    }

                    for (int i = 5; i <= 8; i++){
                        Checkers.w_checkers[i] = new Checkers(i, "white", 2*i );
                        Checkers.w_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[2*i].setOccupied();
                        CheckersSquare.block[2*i].setPieceId(Checkers.w_checkers[i]);
                    }

                    for (int i = 9; i <= 12; i++){
                        Checkers.w_checkers[i] = new Checkers(i, "white", 2*i - 1 );
                        Checkers.w_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[2*i - 1].setOccupied();
                        CheckersSquare.block[2*i - 1].setPieceId(Checkers.w_checkers[i]);
                    }


                    for (int i = 1; i <= 4; i++){
                        Checkers.b_checkers[i] = new Checkers(i, "black", 56 + 2*i  );
                        Checkers.b_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[56 +  2*i ].setOccupied();
                        CheckersSquare.block[56+  2*i ].setPieceId(Checkers.b_checkers[i]);
                    }

                    for (int i = 5; i <= 8; i++){
                        Checkers.b_checkers[i] = new Checkers(i, "black", 40 +  2*i - 1 );
                        Checkers.b_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[ 40 + 2*i - 1].setOccupied();
                        CheckersSquare.block[ 40 + 2*i - 1].setPieceId(Checkers.b_checkers[i]);
                    }

                    for (int i = 9; i <= 12; i++){
                        Checkers.b_checkers[i] = new Checkers(i, "black", 24 + 2*i  );
                        Checkers.b_checkers[i].setCoordinates(0,0);
                        CheckersSquare.block[24 + 2*i ].setOccupied();
                        CheckersSquare.block[24 + 2*i ].setPieceId(Checkers.b_checkers[i]);
                    }

                        /*========================================================*/



                case "join":
                    Runnable game_room = () -> {






                    };
                    executor.execute(game_room);
                    break;
                case "connection_incoming":
                    System.out.print("connection active");
                    break;

                case "sync_data":
                    //To sync f/e and b/e


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
