package com.rhul.springboot;

/*
Acts as the main wrapper for all game funcs in the game, such as game room, player handling etc.
 */

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckersGame {

    private int game_id;
    private String player_name;
    private WebSocketSession session;
    public AtomicInteger player_ids = new AtomicInteger(0);
    public AtomicInteger room_ids = new AtomicInteger(0);

    private ConcurrentHashMap<Integer,Room> rooms_hm = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<Integer, Player>();


    synchronized protected void sendMessage(String msg)  {
        try{
            if(this.session.isOpen())
                this.session.sendMessage(new TextMessage(msg));
        }catch(Exception e){
            e.printStackTrace();

        }
    }

    public boolean check_room_exists(String rm){
        for (Room room : rooms_hm.values()){
            if (room.equals(rm)){
                return true;
            }
        }
        return false;
    }
    //check the room value exists in rm_hm
    public Room get_room(String rm){
        Room rm_obj= null;

        for(ConcurrentHashMap.Entry<Integer,Room> entry : rooms_hm.entrySet()){
            if (entry.getValue().getRoom_name().equals(rm)){
                rm_obj = entry.getValue();
                return rm_obj;
            }
        }
        return rm_obj;
    }






    public void add_rooms(Room rm){
        rooms_hm.put(rm.getRoom_id(),rm);
        room_ids.getAndIncrement();
    }

    public void add_player(Player plyr){
        players_hm.put(plyr.getId(),plyr);
        player_ids.getAndIncrement();
    }



    //this class will include methods on room handling and so on.



}
