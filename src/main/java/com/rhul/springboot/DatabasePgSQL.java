package com.rhul.springboot;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static void fetch_all_rows() throws SQLException {
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("SELECT * FROM leaderboard LIMIT 10");


    }

    public static void create_table() throws SQLException{
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("CREATE TABLE leaderboard (user varchar(255) ,gamesCompleted int,WinPerc varchar(255),LongWinStreak int,GameRank varchar(255))");
    }



    public static void add_rows(){
        Statement stmt = cn.createStatement();


    }


    public static void remove_rows(){



    }

    public static void change_cell_value(){



    }


}
