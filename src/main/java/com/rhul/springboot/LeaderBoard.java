package com.rhul.springboot;

/**This class is responsible for maintaining the users in the leaderboard. Held in a db in docker
 * @Author Azky Mubarack
 *
 * Used as a proxy between playing updating and to reach the database.
 */
public class LeaderBoard {
    private final Player plyr_obj;
    //can use this class to fetch user data and show that on the user status, and game screen
    //e.g. player.leaderboard.get_<field_value>()

    public LeaderBoard(Player plyr){
        this.plyr_obj = plyr;

    }

    public void get_rank(){

    }

    public void games_competed(){

    }

    public void win_percent(){

    }

    public void long_win_streak(){

    }


}
