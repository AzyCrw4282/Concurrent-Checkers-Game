/*
    This class is for the active players of the game

 */
package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class Player {

    public AtomicInteger playerId = new AtomicInteger(0);
    public AtomicInteger roomId = new AtomicInteger(0);

    public static ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<>();
    //hm to keep track of all players in the room

    //lombok used here
    private final int id;
    private final String name;
    private Room room;
    private Thread cur_thread;
    private final WebSocketSession session;
    private int score;
    private int bonus_moves;
    private ScheduledExecutorService scheduler;
    private boolean move_req = false;
    private boolean show_moves_req = false;
    public Checkers checks_obj;
    private int index;//val to make the move
    private int sqr_index;
    private String room_value;
    private String colour;
    private String cur_plyr;

    CheckersGame game = new CheckersGame();

    public Player(int id, String name, WebSocketSession session){
        this.id = id;
        this.name = name;
        this.session = session;
    }


    public synchronized void sendMessage(String msg){
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch (Exception e){

        }

    }

    public Checkers initialize(){

        //so if joining then  give this object reference to other player

        System.out.println("game initializer");
        //used as parent/main objects
        CheckersSquare checks_sqr = new CheckersSquare();
        checks_obj = new Checkers(this, checks_sqr);
        //To fully initialize the game
        //id and session field act as unique in this case

        for (int i = 1; i <= 64; i++) {
            checks_sqr.block[i] = new CheckersSquare(i);//64 objects of squares
        }
        // white counters
        for (int i = 1; i <= 4; i++) {
            checks_obj.w_checkers[i] = new Checkers(i, "white", 2 * i - 1);
            checks_obj.w_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[2 * i - 1].setOccupied();
            checks_sqr.block[2 * i - 1].setPieceId(checks_obj.w_checkers[i]);
        }

        for (int i = 5; i <= 8; i++) {
            checks_obj.w_checkers[i] = new Checkers(i, "white", 2 * i);
            checks_obj.w_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[2 * i].setOccupied();
            checks_sqr.block[2 * i].setPieceId(checks_obj.w_checkers[i]);
        }

        for (int i = 9; i <= 12; i++) {
            checks_obj.w_checkers[i] = new Checkers(i, "white", 2 * i - 1);
            checks_obj.w_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[2 * i - 1].setOccupied();
            checks_sqr.block[2 * i - 1].setPieceId(checks_obj.w_checkers[i]);
        }

        for (int i = 1; i <= 4; i++) {
            checks_obj.b_checkers[i] = new Checkers(i, "black", 56 + 2 * i);
            checks_obj.b_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[56 + 2 * i].setOccupied();
            checks_sqr.block[56 + 2 * i].setPieceId(checks_obj.b_checkers[i]);
        }

        for (int i = 5; i <= 8; i++) {
            checks_obj.b_checkers[i] = new Checkers(i, "black", 40 + 2 * i - 1);
            checks_obj.b_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[40 + 2 * i - 1].setOccupied();
            checks_sqr.block[40 + 2 * i - 1].setPieceId(checks_obj.b_checkers[i]);
        }

        for (int i = 9; i <= 12; i++) {
            checks_obj.b_checkers[i] = new Checkers(i, "black", 24 + 2 * i);
            checks_obj.b_checkers[i].setCoordinates(0, 0);
            checks_sqr.block[24 + 2 * i].setOccupied();
            checks_sqr.block[24 + 2 * i].setPieceId(checks_obj.b_checkers[i]);

        }
        return checks_obj;
    }

    //Each player will have a 1 thread and 1 scheduled thread  for each game
    public void start_game_thread(){
        scheduler = Executors.newScheduledThreadPool(1);//1 scheduled pool for each user
        scheduler.scheduleAtFixedRate(()-> update_game(),1000,1000, TimeUnit.MILLISECONDS);
    }

    public void update_game(){
        //game obj will be shared by 2 players

        if (show_moves_req){
            this.show_moves();
            show_moves_req = false;
        }
        else if (move_req){
            this.make_move();
            move_req = false;
        }
    }

    public void show_moves(){
        //At this point, 2 players in
        //get checks obj from hm.
        if (this.checks_obj == null){
            checks_obj = CheckersGame.get_game_obj(this);
        }


        Room rm = game.get_room(room_value);
        int piece_index = index;//index or id val of the piece
        System.out.println("129");
        //To check for possible moves for a given piece

        if (this.colour.equals("white")) {
            cur_plyr = "white";
            if (checks_obj.show_moves(checks_obj.w_checkers[piece_index], rm)) {//if an attack/move possible
                String mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
                this.sendMessage(mesg);

            }
        } else if (checks_obj.show_moves(checks_obj.b_checkers[piece_index], rm)) {
            cur_plyr = "black";
            System.out.println("black player");
            String mesg = "{\"type\": \"result_move\",\"data\": \"possible\"}";
            this.sendMessage(mesg);
        }
    }

    public void make_move(){

        if (this.checks_obj == null){
            checks_obj = CheckersGame.get_game_obj(this);
        }

        Room rm = game.get_room(room_value);
        int square_index = sqr_index;//index or id val of the piece
        System.out.println("make_move_b/e  call");

        if (this.colour.equals("white")) {
            cur_plyr = "white";
            if (checks_obj.make_move(square_index, cur_plyr, rm)) {//if an attack/move possible
                String mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
                this.sendMessage(mesg);
            }
        } else if (checks_obj.make_move(square_index, cur_plyr, rm)) {
            cur_plyr = "black";
            String mesg = "{\"type\": \"move_made\",\"data\": \"possible\"}";
            this.sendMessage(mesg);
        }


    }

}
