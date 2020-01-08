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
        this.smphore = new Semaphore(4,true);

    }

    public void remove_player_from_room(Player playr){
        players_hm.remove(playr.getId());
        smphore.release();

    }


    public boolean add_player_to_room(Player playr) {
        try {
            if (smphore.availablePermits() == 0) {
                String msg = "{\"type\": \"no_permits_available\"}";
                playr.sendMessage(msg);
            }
            else {

                if (smphore.tryAcquire(5, TimeUnit.SECONDS)) {

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

    public synchronized void apply_to_room_users(String msg,Room rm, Player player){

        if (msg.length() >0){



            for (Player plyr : rm.getPlayers_hm().values()){
                if (this.getRm_owner().getId() == player.getId() || this.getRm_owner().getId()+1 == player.getId()) {
                    System.out.println("plyer id: " + plyr.getId());
                    try {
                        String new_msg = msg + ",\"game_no\":\"1\"}";
                        plyr.sendMessage(new_msg);

                    } catch (Exception e) {
                        e.printStackTrace();
                        remove_player_from_room(plyr);
                    }
                }
                else{
                    try {
                        String new_msg = msg + ",\"game_no\":\"2\"}";
                        plyr.sendMessage(new_msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        remove_player_from_room(plyr);
                    }
                }
            }
        }
    }
}
