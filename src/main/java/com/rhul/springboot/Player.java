/*
    This class is for the active players of the game

 */
package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

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

    public Player(int id, String name, WebSocketSession session){
        this.id = id;
        this.name = name;
        this.session = session;
    }

    private void resetState(){
        this.score = 0;
        this.bonus_moves = 0;


    }


    public synchronized void sendMessage(String msg) throws Exception{
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch (Exception e){

        }

    }





































}
