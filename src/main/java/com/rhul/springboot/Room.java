package com.rhul.springboot;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Time;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**Class represents players present in a room and handles adding/removing users from the room.
 * Each room currently holds upto 8 players. To be improved during further developments....
 * @author Azky
 */
@Getter @Setter
public class Room {

    private ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<Integer, Player>();
    private AtomicInteger players_count = new AtomicInteger(0);
    private Map<String, Boolean> room_games_status= new HashMap<String, Boolean>();
    private Semaphore smphore;
    private int n_games;
    private String room_name;
    private int room_id;
    private Player rm_owner;
    private boolean game_started;

    public Room(int id, String room_nm, Player plyr,int n_games ){
        this.room_id = id;// we use player_id since its an auto incrementer
        this.room_name = room_nm;
        this.rm_owner = plyr;
        this.smphore = new Semaphore(n_games*2,true);//this will be set to max of 8. Need to achive dynamic on handler to get this working
        this.n_games = n_games;
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
                    apply_game_status(this,playr.getName(),players_count.get());
                    check_game_to_start();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            BugsnagConfig.bugsnag().notify(new RuntimeException("Error with semaphore permits acquiring"));
            return  false;
        }
    }
    //if it's an odd player and within less than n games start the game response
    public synchronized  void


    //Method to update game_status on the player's that are present in the room
    public synchronized void apply_game_status(Room rm, String plyr_nm, int players_active){

        for (Player plyr : players_hm.values()){
            try {
                String new_msg = String.format("{\"type\": \"game_status_logs\",\"data\": \"%s joined (%d/%d) active players\"}",plyr_nm,players_active,rm.n_games*2);
                plyr.sendMessage(new_msg);

            } catch (Exception e) {
                e.printStackTrace();
                BugsnagConfig.bugsnag().notify(new RuntimeException("Error in applying game status"));
                remove_player_from_room(plyr);
            }
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
                        BugsnagConfig.bugsnag().notify(new RuntimeException("Error in applying user moves to game 1"));
                        remove_player_from_room(plyr);
                    }
                }
                else{
                    try {
                        String new_msg = msg + ",\"game_no\":\"2\"}";
                        plyr.sendMessage(new_msg);
                    } catch (Exception e) {
                        BugsnagConfig.bugsnag().notify(new RuntimeException("Error in applying user moves to game 2"));
                        e.printStackTrace();
                        remove_player_from_room(plyr);
                    }
                }
            }
        }
    }


}
