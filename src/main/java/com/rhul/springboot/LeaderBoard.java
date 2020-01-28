package com.rhul.springboot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URISyntaxException;
import java.sql.*;

/**This class is responsible for maintaining the users in the leaderboard. Held in a db in docker
 * @Author Azky Mubarack
 *
 * Used as a proxy between playing updating and to reach the database.
 */
@Getter
@Setter
@NoArgsConstructor
public class LeaderBoard {
    private String player_name;//The primary key to identify fields
    //can use this class to fetch user data and show that on the user status, and game screen
    //e.g. player. cn;
    private static Connection cn;
    private String plyr_name;
    private int games_competed;
    private String win_perc;
    private int long_win_streak;
    private String game_ranking;
    private static DatabasePgSQL db = new DatabasePgSQL();
    public LeaderBoard(String player_name){
        this.player_name = player_name;
    }

    public boolean check_if_usr_exists() throws URISyntaxException, SQLException {
        String queryCheck = "SELECT * from messages WHERE msgid = ?";
        PreparedStatement ps = cn.prepareStatement(queryCheck);
        ps.setString(1, this.player_name);
        ResultSet resultSet = ps.executeQuery();

        // if this ID already exists, we quit
        if(resultSet.absolute(1)) {
            //if exists update the values
            plyr_name = resultSet.getString(1);
            games_competed = Integer.parseInt(resultSet.getString(2));
            win_perc = resultSet.getString(3);
            long_win_streak = Integer.parseInt(resultSet.getString(4));
            game_ranking =resultSet.getString(5);
            cn.close();
            return true;
        }
        else{
            return false;
        }

    }

    public  void update_rank(){

    }

    public  void update_games_completed(){

    }

    public void update_win_percent(){

    }

    public void update_long_win_streak(){

    }

}
