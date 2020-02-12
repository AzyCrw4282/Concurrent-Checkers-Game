package com.rhul.springboot.model;

/*
Acts as the main wrapper for all game funcs in the game, such as game room, player handling etc.
 */

import com.rhul.springboot.model.Chat;
import com.rhul.springboot.model.Player;
import com.rhul.springboot.model.Room;
import com.rhul.springboot.model.Checkers;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**Class that handles game instance for each room. 1 instance is used for each game that's present
 * @author Azky
 *
 */
public class CheckersGame {

    public AtomicInteger player_ids = new AtomicInteger(1);
    public AtomicInteger room_ids = new AtomicInteger(1);
    public AtomicInteger lobby_plyrs = new AtomicInteger(1);
    public static ConcurrentHashMap<Player, Checkers> player_game_hm = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer,Player> lobby_chat_players = new ConcurrentHashMap<>();

    private int game_id;
    private String player_name;
    private WebSocketSession session;
    private Chat game_chat = new Chat();
    private static ConcurrentHashMap<Integer, Room> rooms_hm = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<>();

    public synchronized void global_broadcast(String msg){
        game_chat.global_broadcast(msg);
    }

    public synchronized void game_broadcast(String msg){
        game_chat.broadcast(msg);
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

    //This method returns. This method prioritizes room in which there are odd players meaning when another joins a game can be started
    //returns null, or in worst case room with even players
    public String get_room_to_join(){
        String rm_name = null;

        for(ConcurrentHashMap.Entry<Integer,Room> entry : rooms_hm.entrySet()){
            if (entry.getValue().getSmphore().availablePermits() % 2 == 1){
                rm_name = entry.getValue().getRoom_name();
                return rm_name;
            }
            else if(entry.getValue().getSmphore().availablePermits() % 2 == 0) {//even n players present
                rm_name = entry.getValue().getRoom_name();//won't return so can be overridden
            }
        }
        return rm_name;
    }

    public static Checkers get_game_obj(Player plyr){
        int playr_id = plyr.getId();

        if (playr_id % 2 == 1){//Game object is held by player with id of even num.
            playr_id+=1;
        }
        return (get_player_obj(playr_id).checks_obj);
    }

    public static Player get_player_obj(int id){

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

    }

    public void get_rooms_data(WebSocketSession session) throws IOException {

        StringBuilder sb = new StringBuilder();
        System.out.println("Rooms HM "+ rooms_hm.entrySet());
        for (Room rm : rooms_hm.values()){
            //for each room need the id, room name and active players. This should kind of be a string builder
            if ( rm != null){
                sb.append(String.format("{\"game_id\": %s, \"game_name\": \"%s\",\"players_active\":\"%s\",\"max_permits\":\"%s\"}",rm.getRoom_id(),rm.getRoom_name(), rm.getPlayers_count(),rm.getN_games()));
                sb.append(',');
            }
            System.out.println("String vals: " + sb);
        }
        if (sb.length() > 1){
            sb.deleteCharAt(sb.length()-1);//delete the last added ,
        }

        String room_data = String.format("{\"type\": \"room_players_data\",\"data\":[%s]}", sb.toString());
        session.sendMessage(new TextMessage(room_data));
    }

    public boolean is_this_player_game(String room_Value,String game_check_value,Player player){
        //for each and every player check (if > 2 )? False : true -- so other than himself, its nt the game
        Room rm = get_room(room_Value);
        int correct_game_plyr = 0;
        for (Player plyr : rm.getPlayers_hm().values()){
            if (plyr.getGame_number() == Integer.parseInt(game_check_value) && plyr.getId() != player.getId()){
                correct_game_plyr++;
            }
        }
        System.out.println("142 "+ correct_game_plyr);
        return correct_game_plyr != 2;//if 1 its his game,else no
    }

}
