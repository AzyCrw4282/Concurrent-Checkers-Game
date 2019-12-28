package com.rhul.springboot;

import java.util.concurrent.atomic.AtomicInteger;

/**This class will hold variables and methods requires for the chat system. This functionality to be worked on in 2nd term.
 * @author Azky
 */

/** -> Methods to include
 *
 * broadecast msg->for all the existing snakes send a msg
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
        }
    }






























}
