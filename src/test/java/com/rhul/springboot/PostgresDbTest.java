package com.rhul.springboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

import static org.junit.Assert.assertTrue;

public class PostgresDbTest {
    private Connection cn = null;

    @Before
    public void getConnection() throws URISyntaxException, SQLException {
        //use sys.getEnv on production, using with ssl for local dev
        //Alter from here-> https://github.com/heroku/devcenter-java-database
        URI dbUri = new URI("postgres://xydekejapfzoji:91ba62c664c71a78320372982ee54ead86e9bf7a2d17e181e6c685697906770a@ec2-54-220-0-91.eu-west-1.compute.amazonaws.com:5432/d901knl4ie72g6");

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
//        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath()+ "&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
//        System.out.println(dbUrl + " $$$$"+username +"£££££££££ " + password);
        String dbUrl = "jdbc:postgresql://ec2-54-220-0-91.eu-west-1.compute.amazonaws.com/d901knl4ie72g6?user=xydekejapfzoji&password=91ba62c664c71a78320372982ee54ead86e9bf7a2d17e181e6c685697906770a&ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        cn = DriverManager.getConnection(dbUrl, username, password);
        //set assertions here
        assertTrue("Connection has been fetched", cn!=null);

    }

    @Test
    public void insert_data() throws SQLException {
        Statement stmt = cn.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS leaderboard");

        String sql = "CREATE TABLE leaderboard " +
                "(userid VARCHAR(255) not NULL, " +
                " gamescompeted INTEGER, " +
                " winperc VARCHAR(255), " +
                " longwinstreak INTEGER, " +
                " gamerank VARCHAR(255), " +
                " PRIMARY KEY ( userid ))";
        stmt.executeUpdate(sql);
//        stmt.executeUpdate("CREATE TABLE leaderboard (user varchar(255) NOT NULL,gamesCompeted int,WinPerc varchar(255),LongWinStreak int,GameRank varchar(255))");
        stmt.executeUpdate("INSERT INTO leaderboard VALUES ('Meking',28,'57%',7,'Hero')");
        stmt.executeUpdate("INSERT INTO leaderboard VALUES ('user4334',52,'63%',9,'Veteran')");
        stmt.executeUpdate("INSERT INTO leaderboard VALUES ('tryingBe',183,'57%',15,'Grand Master')");
        stmt.executeUpdate("INSERT INTO leaderboard VALUES ('UltimatePlayer',5,'40%',2,'Newbie')");

//        assertTrue("Insert and fetch data validity",expected_out != "");//check something has been fetched

    }
//        stmt.executeUpdate("CREATE TABLE ticks (tick timestamp )");
//        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
//        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");





}
