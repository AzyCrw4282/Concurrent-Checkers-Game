package com.rhul.springboot.model;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

/**This class is used to initialize, add remove data from remote heroku db.
 * @Author Azky
 */
public class DatabasePgSQL {
    static Connection cn;

    public static Connection create_connection() throws URISyntaxException, SQLException {
        URI dbUri = new URI("postgres://xydekejapfzoji:91ba62c664c71a78320372982ee54ead86e9bf7a2d17e181e6c685697906770a@ec2-54-220-0-91.eu-west-1.compute.amazonaws.com:5432/d901knl4ie72g6");
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://ec2-54-220-0-91.eu-west-1.compute.amazonaws.com/d901knl4ie72g6?user=xydekejapfzoji&password=91ba62c664c71a78320372982ee54ead86e9bf7a2d17e181e6c685697906770a&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        cn = DriverManager.getConnection(dbUrl, username, password);
        return cn;
    }

    public static void fetch_all_rows(WebSocketSession session) throws SQLException, IOException, URISyntaxException {
        cn = create_connection();
        Statement stmt = cn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM leaderboard ORDER BY gamescompeted DESC LIMIT 10");
        System.out.println("line 31 "+ rs);
        StringBuilder sb = new StringBuilder();
        //Add it to a 2d array method and send it to user

        while (rs.next()){
              sb.append(String.format("{\"user_id\": \"%s\", \"games_competed\": \"%s\",\"win_percent\":\"%s\" ,\"long_win_streak\": \"%s\",\"game_ranking\": \"%s\"}",rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)));
              sb.append(",");
        }
        if (sb.length() > 1){
            sb.deleteCharAt(sb.length()-1);//delete the last added ,
        }
        System.out.println(sb);
        String room_data = String.format("{\"type\": \"leaderboard_resp\",\"data\":[%s]}", sb.toString());
        session.sendMessage(new TextMessage(room_data));

    }

    public static void create_table() throws SQLException{
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("CREATE TABLE leaderboard (user varchar(255) ,gamesCompeted int,WinPerc varchar(255),LongWinStreak int,GameRank varchar(255))");
    }

    public static void add_rows()throws SQLException{
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("");

    }

}
