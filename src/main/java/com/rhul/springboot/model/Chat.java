package com.rhul.springboot.model;

import com.rhul.springboot.utils.BugsnagConfig;

import java.util.concurrent.atomic.AtomicInteger;

/**This class will hold variables and methods requires for the chat system. This functionality to be worked on in 2nd term.
 * @author Azky
 */

/** -> Methods to include
 *
 * broadecast msg->for all the existing snakes send a msgs
 *
 *
 */

public class Chat  {
    private AtomicInteger counter;

    public Chat(){
        counter = new AtomicInteger(0);
    }

    public synchronized void broadcast(String msg){
        try{

            for(Player plyr : Player.players_hm.values()){
                plyr.sendMessage(msg);
            }

        }catch (Exception e){
            e.printStackTrace();
            BugsnagConfig.bugsnag().notify(new RuntimeException("Broadcasting message error"));
        }
    }

    public synchronized void global_broadcast(String msg){
        try{

            for(Player plyr : CheckersGame.lobby_chat_players.values()){
                plyr.sendMessage(msg);
                System.out.println("40");
            }

        }catch (Exception e){
            e.printStackTrace();
            BugsnagConfig.bugsnag().notify(new RuntimeException("Global broadcast message error"));
        }
    }






























}
