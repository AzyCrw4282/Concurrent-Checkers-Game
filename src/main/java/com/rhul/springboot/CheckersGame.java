package com.rhul.springboot;

/*
Acts as the main wrapper for all game funcs in the game, such as game room, player handling etc.
 */

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**Class that handles game instance for each room. 1 instance is used for each game that's present
 * @author Azky
 *
 */
public class CheckersGame {

    private int game_id;
    private String player_name;
    private WebSocketSession session;
    private Chat global_chat = new Chat();
    public AtomicInteger player_ids = new AtomicInteger(1);//all starts at 1
    public AtomicInteger room_ids = new AtomicInteger(1);

    //keeps which players are associated to which game may need
    public static ConcurrentHashMap<Player,Checkers> player_game_hm = new ConcurrentHashMap<>();//Able to get the game and append it to string
    private static ConcurrentHashMap<Integer,Room> rooms_hm = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<>();


    public synchronized void global_broadcast(String msg){
        global_chat.broadcast(msg);
    }

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
            System.out.println(room.getRoom_name());
            if (room.getRoom_name().equals(rm)){
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

    //returns game obj, so if player is in that game object then return
    // Or game obj is always held by the user with id =2,4 so retunr that

    public static Checkers get_game_obj(Player plyr){
        int playr_id = plyr.getId();

        if (playr_id <3){//first game
            return (get_player_obj(2).checks_obj);
        }
        else if (playr_id > 2 && playr_id < 5){
            return (get_player_obj(4).checks_obj);
        }
        return null;

    }

    public static Player get_player_obj(int id){

        //iterate through hm and check for the correct val of id
        for (Player p : Player.players_hm.values()){
            if (p.getId() == id){
                return p;
            }
        }
        return null;

    }

    public void add_rooms(Room rm){
        rooms_hm.put(rm.getRoom_id(),rm);
        room_ids.getAndIncrement();
    }

    public void add_player(Player plyr){
        players_hm.put(plyr.getId(),plyr);
//        player_ids.getAndIncrement();
    }



    //this class will include methods on room handling and so on.



}
