package com.rhul.springboot;
/*
Class responsible for adding/removing users from the room
 */
import lombok.Getter;
import lombok.Setter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class ChatRoom {
    /*
    need:
        concurrent hashmap
        Atomic integer
        f for remove play freom room
        f for add player to a room
        f for send msg to all users in room
        f remove user from the room
        Check the use of getters/setters(generated at compile time so not eneded to write in class

     */

    private ConcurrentHashMap<Integer,Player> players_hm = new ConcurrentHashMap<Integer, Player>();

    AtomicInteger counter;

    private void initializeChat(){
        counter = new AtomicInteger(0);

    }


    private void addPlayerToRoom(Player playr) throws Exception{
        players.put(playr.getId(), playr);
        counter.getAndIncrement();
        System.out.println("----------------------roomLIST-----------------");

        for (Player p : players_hm.values()) {
            System.out.println(p.getId());
        }
        System.out.println("----------------------FIN-----------------");
    }

    private void sendMsgToAllPlayers(String message){
        for (Player plyr : players_hm.values()) {
            try {
                plyr.sendMessage(message);

            } catch (Throwable ex) {
                System.err.println("Execption sending message to snake " + snake.getId());
                ex.printStackTrace(System.err);
            }
        }
    }

    //Remove a player if it exists
    public boolean removePlayerIfExists(String n){
        for (Player plyr : players_hm.values()){
            if (plyr.getName().equals(n)){
                players.remove(Integer.valueOf(plyr.getId()));
                return true;
            }
        }
        return false;
    }

    private void eliminatePlayerFromRoom(Player playr){
        players_hm.remove(Integer.valueOf(playr.getID()));
        counter.getAndDecrement();

    }

















}
