package com.rhul.springboot;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class CheckersGame {

    public CheckersGame(int id, WebSocketSession session, String name){
        this.id = id;
        this.session = session;
        this.player_name = name;

    }

    synchronized protected void sendMessage(String msg)  {
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch(Exception e){
            e.printStackTrace();

        }
    }

}
