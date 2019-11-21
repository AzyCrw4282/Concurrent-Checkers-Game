package com.rhul.springboot;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import static org.junit.Assert.assertTrue;
import java.net.URI;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentCheckersApplicationTests {

    private AtomicInteger counter = new AtomicInteger(0);
    private static AtomicInteger static_counter = new AtomicInteger(0);


    /**
     * Starts and intitalizes server and its a pre-requisite for all tests
     */
    @BeforeClass
    public static void start_server() {
        ConcurrentCheckersApplication.main(new String[]{
                "--server.port=8080"});
    }

    /**
     * @throws Exception
     */
    @Test
    public void test_connection() throws Exception {
        System.out.println("----------------------------Testing connection-------------------");
        WebSocketTest Wsc = new WebSocketTest();
        Wsc.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
        System.out.println("Successfully connected");
        Wsc.disconnect();
    }

    /**
     * This test is used to check players joining  joining upto 4.
     *
     * @throws Exception if any joining errors occur
     */

    @Test
    public void test_join() throws Exception {
        //steps - > connect, if coutner firstr then create room requ. else join req
        //send the msg of
        //and then finish request
        //conenction disconnet
        CyclicBarrier c_barrier = new CyclicBarrier(4);
        Executor execut = Executors.newFixedThreadPool(4);

        AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
        WebSocketTest ws = new WebSocketTest();
        //msg from back-end, i.e. responses


        Runnable area = () -> {
            int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);
            //for each runnable thread we get the running id of it
            ws.onMessage((session, message) -> {
                array.compareAndSet(id, null, message);//msg is then set in the string array
                System.out.println("array id " + id);
                System.out.println("Msg from back-end " + message);
            });

            try {
                ws.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
                String message;

                //All users being tested are joining the same room.
                if (counter.incrementAndGet() == 1) {
                    //value hard_coded for the purpose fo testing
                    message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 234);
                } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {//joining users opp, automatically initz game
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 234);
                } else {//joinig user of second game
                    System.out.println("Joining user of the second game");
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 234);
                }

                ws.sendMessage(message);
                Thread.sleep(2000);
                message = "{\"type\": \"game_finish\"}";
                ws.sendMessage(message);
                ws.disconnect();
                c_barrier.await();


            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Test failed");
            }
        };
        for (int i = 0; i < 4; i++) {
            execut.execute(area);
            Thread.sleep(1500);
        }
        c_barrier.await();

        for (int i = 0; i < 4; i++) {
            String array_msg = array.get(i);
            if (i == 0) {//rm owner
                assertTrue("Room owner returned 'OK' " + array_msg, array_msg.contains("Ok"));
            } else {//joining users
                assertTrue("Joining room users returned 'successful' " + array_msg, array_msg.contains("successful"));
            }
        }

    }

    /**
     * start game procedure tested for all players(max 4 atm)
     *
     * @throws Exception
     */
    @Test
    public void test_start_game() throws Exception {

        CyclicBarrier c_barrier = new CyclicBarrier(2);
        Executor execut = Executors.newFixedThreadPool(2);

        AtomicReferenceArray<String> array = new AtomicReferenceArray(2);
        counter.set(0);
        //msg from back-end, i.e. responses

        Runnable area = () -> {

            int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);
            System.out.println("The id value "+ id);
            //for each runnable thread we get the running id of it
            WebSocketTest wsc = new WebSocketTest();
            wsc.onMessage((session, message) -> {
//                    array2.compareAndSet(id, null, message);//msg is then set in the string array
                if (message.contains("rdy")){
                    array.set(id, message);
                    System.out.println("Array id " + id);
                    System.out.println("Msg from server " + message);
                }
            });

            try {
                wsc.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
                String message;

                if (counter.incrementAndGet() == 1) {
                    //value hard_coded for the purpose fo testing
                    message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {//joining users opp, automatically initz game
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                } else {//joinig user of second game
                    System.out.println("Joining user of the second game");
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                }
                System.out.println("Game Start test for each player");
                message = String.format("{\"type\": \"start_game\", \"player_id\":\"%d\",\"room_value\":\"123\"}", id+1);
                wsc.sendMessage(message);
                Thread.sleep(2000);
                message = "{\"type\": \"game_finish\"}";
                wsc.sendMessage(message);
                wsc.disconnect();
                c_barrier.await();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Test failed");
            }
        };
        for (int i = 0; i < 2; i++) {
            execut.execute(area);
            Thread.sleep(1500);
        }
        c_barrier.await();

        for (int i = 0; i < 2; i++) {
            String array_msg = array.get(i);
                assertTrue("Game_star status " + array_msg, array_msg.contains("rdy"));
        }

    }
    /**Send game finish msg to back-end to end the game
     *
     *
     */
    @Test
    public void test_finish ()  throws Exception {
        CyclicBarrier c_barrier = new CyclicBarrier(5);
        Executor execut = Executors.newFixedThreadPool(4);

        AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
        counter.set(0);
        //msg from back-end, i.e. responses

        Runnable area = () -> {

            int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);

            //For each runnable thread we get the running id of it
            WebSocketTest wsc = new WebSocketTest();
            wsc.onMessage((session, message) -> {
//                    array2.compareAndSet(id, null, message);//msg is then set in the string array
                if (message.contains("game_terminated")){
                    array.set(id, message);
                    System.out.println("Array id " + id);
                    System.out.println("Msg from server " + message);
                }
            });

            try {
                wsc.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
                String message;

                if (counter.incrementAndGet() == 1) {
                    //value hard_coded for the purpose fo testing
                    message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {//joining users opp, automatically initz game
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                } else {//joinig user of second game
                    System.out.println("Joining user of the second game");
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 123);
                    wsc.sendMessage(message);
                }

                System.out.println("Game Start test for each player");
                Thread.sleep(2000);
                c_barrier.await();
                message = String.format("{\"type\": \"game_finish\", \"player_id\":\"%d\",\"room_value\":\"123\"}", id);
                wsc.sendMessage(message);
                Thread.sleep(1000);
                wsc.disconnect();
                c_barrier.await();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Test failed");
            }
        };

        for (int i = 0; i < 4; i++) {
            execut.execute(area);
            Thread.sleep(1500);
        }
        c_barrier.await();
        Thread.sleep(2500);
        for (int i = 0; i < 4; i++) {
            String array_msg = array.get(i);
            assertTrue("Game_star status " + array_msg, array_msg.contains("game_terminated"));
            System.out.println("Asserting checking values for i "+ i);
        }

    }


    @Test
    public void test_show_moves () throws Exception {


    }
    @Test
    public void test_make_moves ()  throws Exception{


    }

    @Test
    public void test_get_room_permits () throws Exception {


    }

    @Test
    public void test_screen_size_change () throws Exception {


    }

}
