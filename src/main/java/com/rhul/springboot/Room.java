package com.rhul.springboot;
/*
Class responsible for adding/removing users from the room
 */
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Time;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class Room {

    private ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<Integer, Player>();
    public AtomicInteger players_count = new AtomicInteger(0);
    private Semaphore smphore;
    private String room_name;
    private int room_id;
    private Player player_obj;
    private boolean game_started;

    AtomicInteger counter;


    public Room(int id, String room_nm, Player plyr ){
        this.room_id = id;
        this.room_name = room_nm;
        this.player_obj = plyr;
        this.smphore = new Semaphore(4,true);//max allowed & first in first out also set

    }


    public void remove_player_from_room(Player playr){
        players_hm.remove(playr.getId());
        smphore.release();


    }


    public boolean add_player_to_room(Player playr) throws Exception {
        try {
            if (smphore.availablePermits() == 0) {
                String msg = "{\"type\": \"no_permits_available\"}";
                playr.sendMessage(msg);
            }
            else {//if permits exists then add a player to a room
                //try to aquire the lock and then add it hm and print all plyer in hm return bool val
                if (smphore.tryAcquire(5, TimeUnit.SECONDS)) {
                    //if acquired then
                    players_hm.put(playr.getId(), playr);
                    players_count.getAndIncrement();
                    for (Player p : players_hm.values()) {
                        System.out.println(p);
                    }
                    return true;

                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error with semaphore permits acquiring. ");
            return  false;
        }

    }


}
