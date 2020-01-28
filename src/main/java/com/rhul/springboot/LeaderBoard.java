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
    private String plyr_name;
    private int games_competed;
    private String win_perc;
    private int long_win_streak;
    private String game_ranking;
    private String games_lost;
    private String cur_win_streak;
    private static DatabasePgSQL db = new DatabasePgSQL();
    private static Connection cn;
    public LeaderBoard(String player_name) throws URISyntaxException, SQLException {
        this.player_name = player_name;
        cn = db.create_connection();
    }

    public boolean check_if_usr_exists() throws SQLException {
        String queryCheck = "SELECT * from leaderboard WHERE userid = ?";
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
            game_ranking = resultSet.getString(5);
            games_lost = resultSet.getString(6);
            cur_win_streak = resultSet.getString(7);
            cn.close();
            return true;
        }
        else{
            return false;
        }

    }
    public boolean create_new_user() throws SQLException {
        Statement stmt = cn.createStatement();
        String query = ("INSERT INTO leaderboard VALUES (?,0,'0%',0,'Newbie')");
        PreparedStatement ps = cn.prepareStatement(query);
        ps.setString(1,this.player_name);
        if (ps.executeUpdate() == 1) return true;
        else return false;
    }

    /*
    <20 - newbie
    <50 - Hero
    <150 - Veteran
    >= 150 Grandmaster
     */
    public void update_rank() throws SQLException {
        String prev_game_ranking = game_ranking;
        if (this.games_competed + 1 < 20){
            game_ranking = "Newbie";
        }
        else if (this.games_competed + 1 < 50){
            game_ranking = "Hero";
        }
        else if (this.games_competed + 1 < 150){
            game_ranking = "Veteran";
        }
        else{
            game_ranking = "Grandmaster";
        }
        //validating that new ranking is differnt to the old one
        if (!prev_game_ranking.equals(game_ranking)){
            String queryCheck = "UPDATE leaderboard SET gamerank = ? WHERE userid = ?;";
            PreparedStatement ps = cn.prepareStatement(queryCheck);
            ps.setString(1, this.game_ranking);
            ps.setString(2,this.player_name);
            ps.executeQuery();
        }

    }

    public void update_games_competed() throws SQLException {
        this.games_competed +=1;
        String queryCheck = "UPDATE leaderboard SET games_competed = ? WHERE userid = ?;";
        PreparedStatement ps = cn.prepareStatement(queryCheck);
        ps.setString(1, String.valueOf(this.games_competed));
        ps.setString(2,this.player_name);
        ps.executeQuery();

    }

    public void update_win_percent() throws SQLException {
        int calc_win_percent = (Integer.parseInt(String.valueOf(games_competed)) - Integer.parseInt(games_lost))/((Integer.parseInt(String.valueOf(games_competed))));
        String queryCheck = "UPDATE leaderboard SET winperc = ? WHERE userid = ?;";
        PreparedStatement ps = cn.prepareStatement(queryCheck);
        ps.setString(1, String.valueOf(calc_win_percent));
        ps.setString(2,this.player_name);
        ps.executeQuery();

    }

    public void update_long_win_streak() throws SQLException {
        int cur_streak = Integer.parseInt(String.valueOf(cur_win_streak)+1);
        if (cur_streak>(Integer.parseInt(String.valueOf(long_win_streak)))){
            String queryCheck = "UPDATE leaderboard SET longwinstreak = ? WHERE userid = ?;";
            PreparedStatement ps = cn.prepareStatement(queryCheck);
            ps.setString(1, String.valueOf(cur_streak));
            ps.setString(2,this.player_name);
            ps.executeQuery();
        }
    }
}
