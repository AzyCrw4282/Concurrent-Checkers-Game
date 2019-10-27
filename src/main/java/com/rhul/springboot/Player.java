package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/*
class includes following vars:
player id
player name
player room
thread var(known why)
WSS


methods:


constr
restores all moves and values, whem game state is end
scores for each move captured
msg sending
checkMade
points
getters and setters to be @sync form without lombok


 */
@Getter @Setter
public class Player {

    public AtomicInteger playerId = new AtomicInteger(0);
    public AtomicInteger roomId = new AtomicInteger(0);

    private final int id;
    private final String name;
    private ChatRoom room;
    private Thread thread;
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



    //getters and setters achieved through lombok
    //lombok not working!!!
     public int getId(){
        return id;
     }

































}
