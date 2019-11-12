/*
    This class is for the active players of the game

 */
package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class Player {

    public AtomicInteger playerId = new AtomicInteger(0);
    public AtomicInteger roomId = new AtomicInteger(0);


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

    public Player(int id, String name, WebSocketSession session){
        this.id = id;
        this.name = name;
        this.session = session;
    }

    private void resetState(){
        this.score = 0;
        this.bonus_moves = 0;

    }

    public synchronized void sendMessage(String msg){
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch (Exception e){

        }

    }



    //Each player will have a scheduled thread
    public void start_game_thread(){
        scheduler = Executors.newScheduledThreadPool(1);//1 scheduled pool for each user
        scheduler.scheduleAtFixedRate(()->update_game(),1000,1000, TimeUnit.MILLISECONDS);
    }


    public void update_game(){
        //game obj will be shared by 2 players
        if (show_moves_req){
            show_moves();
            show_moves_req = false;
        }
        else if (move_req){
            make_move();
            move_req = false;
        }

    }

    public void show_moves(){



    }

    public void make_move(){




    }






}
