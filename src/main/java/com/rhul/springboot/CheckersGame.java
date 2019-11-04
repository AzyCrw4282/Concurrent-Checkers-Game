package com.rhul.springboot;

/*
Acts as the main wrapper for all game funcs in the game, such as game room, player handling etc.
 */

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckersGame {

    private int game_id;
    private String player_name;
    private WebSocketSession session;
    public AtomicInteger player_ids = new AtomicInteger(0);
    public AtomicInteger room_ids = new AtomicInteger(0);




    synchronized protected void sendMessage(String msg)  {
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch(Exception e){
            e.printStackTrace();

        }
    }

    //this class will include methods on room handling and so on.



}
