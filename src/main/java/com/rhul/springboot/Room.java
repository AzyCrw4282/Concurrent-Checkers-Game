package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Time;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**Class represents players present in a room and handles adding/removing users from the room.
 * Each room currently holds upto 4 players.
 * @author Azky
 */
@Getter @Setter
public class Room {
    //cotnains the players in the room
    private ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<Integer, Player>();
    public AtomicInteger players_count = new AtomicInteger(0);
    private Semaphore smphore;
    private String room_name;
    private int room_id;
    private Player rm_owner;
    private boolean game_started;

    AtomicInteger counter;

    public Room(int id, String room_nm, Player plyr ){
        this.room_id = id;
        this.room_name = room_nm;
        this.rm_owner = plyr;
        this.smphore = new Semaphore(4,true);//max allowed & first in first out also set

    }

    public void remove_player_from_room(Player playr){
        players_hm.remove(playr.getId());
        smphore.release();

    }


    public String get_room_players(){
        //return the name of game, players active for each room




    }


    public boolean add_player_to_room(Player playr) {
        try {
            if (smphore.availablePermits() == 0) {
                String msg = "{\"type\": \"no_permits_available\"}";
                playr.sendMessage(msg);
            }
            else {//if permits exists then add a player to a room
                //try to acquire the lock and then add it hm and print all plyer in hm return bool val
                if (smphore.tryAcquire(5, TimeUnit.SECONDS)) {
                    //if acquired then
                    players_hm.put(playr.getId(), playr);
                    players_count.getAndIncrement();
                    for (Player p : players_hm.values()) {
                        System.out.println("players in room :" + p.getId());
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
    //this method is responsible for applying moves to f/e for all users in a room
    public synchronized void apply_to_room_users(String msg,Room rm, Player player){

        if (msg.length() >0){
            //needs to be applied only on the subset of the rm users

//                0 and 1 player id for game 1 or 2 and 3 for game 2
            for (Player plyr : rm.getPlayers_hm().values()){
                if (this.getRm_owner().getId() == player.getId() || this.getRm_owner().getId()+1 == player.getId()) {//if game 1
                    System.out.println("plyer id: " + plyr.getId());
                    try {
                        String new_msg = msg + ",\"game_no\":\"1\"}";
                        plyr.sendMessage(new_msg);
                        //append a msg here if game 1/2
                    } catch (Exception e) {
                        e.printStackTrace();
                        remove_player_from_room(plyr);//error handling to ensure player that left the room is removed
                    }
                }
                else{//2nd game, with player id's of 2,3
                    try {
                        String new_msg = msg + ",\"game_no\":\"2\"}";
                        plyr.sendMessage(new_msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        remove_player_from_room(plyr);//error handling to ensure player that left the room is removed
                    }
                }
            }
        }
    }
}
