package com.rhul.springboot;

/**This class is responsible for maintaining the users in the leaderboard. Held in a db in docker
 * @Author Azky Mubarack
 *
 * Used as a proxy between playing updating and to reach the database.
 */
public class LeaderBoard {
    private final Player plyr_obj;
    private final String player_name;//The primary key to identify fields
    //can use this class to fetch user data and show that on the user status, and game screen
    //e.g. player.leaderboard.get_<field_value>()

    public LeaderBoard(Player plyr,String player_name){
        this.plyr_obj = plyr;
        this.player_name = player_name;

    }

    public void get_rank(){


    }

    public void get_games_competed(){

    }

    public void get_win_percent(){

    }

    public void get_long_win_streak(){

    }

    public void update_rank(){

    }

    public void update_games_completed(){

    }

    public void update_win_percent(){

    }

    public void update_long_win_streak(){

    }

}
