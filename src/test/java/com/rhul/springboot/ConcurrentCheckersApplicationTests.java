
// package com.rhul.springboot;

// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
// import org.springframework.util.concurrent.ListenableFuture;
// import org.springframework.web.socket.WebSocketHandler;
// import org.springframework.web.socket.WebSocketHttpHeaders;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.client.WebSocketClient;
// import static org.junit.Assert.assertTrue;
// import java.net.URI;
// import java.util.concurrent.CyclicBarrier;
// import java.util.concurrent.Executor;
// import java.util.concurrent.Executors;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.atomic.AtomicReferenceArray;

// //To add Behaviour tests and others.....

// /** This is the main test class that performs all TDD tests required for all functionalities in the program.
//  * @author Azky
//  */
// @RunWith(SpringRunner.class)
// @SpringBootTest
// public class ConcurrentCheckersApplicationTests {

//     private AtomicInteger counter = new AtomicInteger(0);

//     /**
//      * Starts and intitalizes server and its a pre-requisite for all tests
//      */
//     @BeforeClass
//     public static void start_server() {
//         ConcurrentCheckersApplication.main(new String[]{
//                 "--server.port=8080"});
//     }

//     /**
//      * @throws Exception
//      */
//     @Test
//     public void test_connection() throws Exception {
//         System.out.println("----------------------------Testing connection-------------------");
//         WebSocketTest Wsc = new WebSocketTest();
//         Wsc.connect("wss://springboot21.herokuapp.com/springboot");//--TBC for when deployed to heroku
//         System.out.println("Successfully connected");
//         Wsc.disconnect();
//     }

//     /**
//      * This test is used to check players joining  joining upto 4.
//      *
//      * @throws Exception if any joining errors occur
//      */

//     @Test
//     public void test_join() throws Exception {

//         CyclicBarrier c_barrier = new CyclicBarrier(4);
//         Executor execut = Executors.newFixedThreadPool(4);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
//         WebSocketTest ws = new WebSocketTest();


//         Runnable area = () -> {
//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);

//             ws.onMessage((session, message) -> {
//                 array.compareAndSet(id, null, message);
//                 System.out.println("array id " + id);
//                 System.out.println("Msg from back-end " + message);
//             });

//             try {
//                 ws.connect("ws://127.0.0.1:8080/springboot");
//                 String message;


//                 if (counter.incrementAndGet() == 1) {

//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 234);
//                 } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 234);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 234);

//                 }

//                 ws.sendMessage(message);
//                 Thread.sleep(2000);
//                 message = "{\"type\": \"game_finish\"}";
//                 ws.sendMessage(message);
//                 ws.disconnect();
//                 c_barrier.await();


//             } catch (Exception e) {
//                 e.printStackTrace();

//                 System.out.println("Test failed");
//             }
//         };
//         for (int i = 0; i < 4; i++) {
//             execut.execute(area);
//             Thread.sleep(1500);
//         }
//         c_barrier.await();

//         for (int i = 0; i < 4; i++) {
//             String array_msg = array.get(i);
//             if (i == 0) {
//                 assertTrue("Room owner returned 'OK' " + array_msg, array_msg.contains("Ok"));
//             } else {
//                 assertTrue("Joining room users returned 'successful' " + array_msg, array_msg.contains("successful"));
//             }
//         }
//     }

//     /**
//      * start game procedure tested for all players(max 4 atm)
//      *
//      * @throws Exception
//      */
//     @Test
//     public void test_start_game() throws Exception {

//         CyclicBarrier c_barrier = new CyclicBarrier(2);
//         Executor execut = Executors.newFixedThreadPool(2);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(2);
//         counter.set(0);


//         Runnable area = () -> {

//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);
//             System.out.println("The id value "+ id);

//             WebSocketTest wsc = new WebSocketTest();
//             wsc.onMessage((session, message) -> {

//                 if (message.contains("rdy")){
//                     array.set(id, message);
//                     System.out.println("Array id " + id);
//                     System.out.println("Msg from server " + message);
//                 }
//             });

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;

//                 if (counter.incrementAndGet() == 1) {

//                     message =("{\"type\": \"update_player_id\"}");
//                     wsc.sendMessage(message);
//                     Thread.sleep(2000);
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 999);
//                     wsc.sendMessage(message);
//                 } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 999);
//                     wsc.sendMessage(message);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 999);
//                     wsc.sendMessage(message);
//                 }
//                 c_barrier.await();
//                 Thread.sleep(2500);
//                 System.out.println("Game Start test for each player");
//                 message = String.format("{\"type\": \"start_game\", \"player_id\":\"%d\",\"room_value\":\"999\"}", id+1);
//                 wsc.sendMessage(message);
//                 Thread.sleep(2000);
//                 message = "{\"type\": \"game_finish\"}";
//                 wsc.sendMessage(message);
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 2; i++) {
//             execut.execute(area);
//             Thread.sleep(3500);
//         }
//         c_barrier.await();
//         Thread.sleep(2500);
//         for (int i = 0; i < 2; i++) {
//             String array_msg = array.get(i);
//             assertTrue("Game_star status " + array_msg, array_msg.contains("rdy"));
//         }
//     }

//     /**
//      * Tests the show_moves functioanlity. I will pass in a hard coded value of index to perform this test.
//      * @throws Exception
//      */
//     @Test
//     public void test_show_moves () throws Exception {
//         CyclicBarrier c_barrier = new CyclicBarrier(5);
//         Executor execut = Executors.newFixedThreadPool(4);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
//         counter.set(0);


//         Runnable area = () -> {
//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);


//             WebSocketTest wsc = new WebSocketTest();
//             wsc.onMessage((session, message) -> {

//                 if (message.contains("result_move")) {
//                     array.set(id, message);
//                     System.out.println("Array id " + id);
//                     System.out.println("Msg from server " + message);
//                 }
//             });

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;

//                 if (counter.incrementAndGet() == 1) {

//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 345);
//                     wsc.sendMessage(message);
//                 } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 345);
//                     wsc.sendMessage(message);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 345);
//                     wsc.sendMessage(message);
//                 }

//                 System.out.println("Show moves tests for each player");
//                 Thread.sleep(2000);
//                 c_barrier.await();
//                 message = String.format("{\"type\": \"show_moves\", \"player_id\":\"%d\",\"room_value\":\"123\",\"index\": \"%d\"}", id+1,11);
//                 wsc.sendMessage(message);
//                 Thread.sleep(1000);
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 4; i++) {
//             execut.execute(area);
//             Thread.sleep(3500);
//         }
//         c_barrier.await();
//         Thread.sleep(5500);
//         for (int i = 0; i < 4; i++) {
//             String array_msg = array.get(i);
//             assertTrue("Game_star status " + array_msg, array_msg.contains("result_move"));
//             System.out.println("Asserting checking values for i "+ i);
//         }
//     }

//     /**
//      * Tests done for white coloured players, subject to the chosen index val in earlier test
//      * @throws Exception
//      */

//     @Test
//     public void test_make_move()  throws Exception {
//         CyclicBarrier c_barrier = new CyclicBarrier(3);
//         Executor execut = Executors.newFixedThreadPool(2);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(2);
//         counter.set(0);


//         Runnable area = () -> {

//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);


//             WebSocketTest wsc = new WebSocketTest();
//             wsc.onMessage((session, message) -> {

//                 if (message.contains("non_attack_move")) {
//                     array.set(id, message);
//                     System.out.println("Array id " + id);
//                     System.out.println("Msg from server " + message);
//                 }
//             });

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;

//                 if (counter.incrementAndGet() == 1) {

//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 456);
//                     wsc.sendMessage(message);
//                 } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 456);
//                     wsc.sendMessage(message);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 456);
//                     wsc.sendMessage(message);
//                 }

//                 System.out.println("show move is applied and then make move tests for each player");
//                 Thread.sleep(2000);
//                 if (id == 0){
//                     message = String.format("{\"type\": \"show_moves\", \"player_id\":\"%d\",\"room_value\":\"123\",\"index\": \"%d\"}", id+1,11);
//                     wsc.sendMessage(message);
//                     c_barrier.await();
//                     Thread.sleep(2000);
//                     message = String.format("{\"type\": \"make_move\", \"player_id\":\"%d\",\"room_value\":\"123\",\"sqr_index\": \"%d\"}", id+1,30);
//                     wsc.sendMessage(message);
//                     Thread.sleep(1000);
//                 }
//                 else if (id == 1){
//                     message = String.format("{\"type\": \"show_moves\", \"player_id\":\"%d\",\"room_value\":\"123\",\"index\": \"%d\"}", id+1,11);
//                     wsc.sendMessage(message);
//                     c_barrier.await();
//                     Thread.sleep(2000);
//                     message = String.format("{\"type\": \"make_move\", \"player_id\":\"%d\",\"room_value\":\"123\",\"sqr_index\": \"%d\"}", id+1,37);
//                     wsc.sendMessage(message);
//                     Thread.sleep(1000);
//                 }
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 2; i++) {
//             execut.execute(area);
//             Thread.sleep(3500);
//         }
//         c_barrier.await();
//         Thread.sleep(3500);
//         for (int i = 0; i < 2; i++) {
//             String array_msg = array.get(i);
//             assertTrue("Game_star status " + array_msg, array_msg.contains("non_attack_move"));
//             System.out.println("Asserting checking values for i "+ i);
//         }

//     }



//     /**Test is done to get the value of the remaining permits held by the semaphore. This is important as
//      * without this mechanism it would be impossible to know if the user should be player 2 or in game 1/2
//      *
//      * @throws Exception
//      */
//     @Test
//     public void test_get_room_permits() throws Exception {
//         CyclicBarrier c_barrier = new CyclicBarrier(5);
//         Executor execut = Executors.newFixedThreadPool(4);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
//         AtomicInteger counter2 = new AtomicInteger(0);



//         Runnable area = () -> {

//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);


//             WebSocketTest wsc = new WebSocketTest();
//             wsc.onMessage((session, message) -> {

//                 if (message.contains("room_permits")) {
//                     array.set(id, message);
//                     System.out.println("Array id " + id);
//                     System.out.println("Msg from server " + message);
//                 }
//             });

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;

//                 if (counter2.incrementAndGet() == 1) {

//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 567);
//                     wsc.sendMessage(message);
//                     Thread.sleep(5000);
//                     message = String.format("{\"type\": \"get_room_permits\",\"room_value\":\"567\"}");
//                     wsc.sendMessage(message);
//                 } else if (counter2.incrementAndGet() == 2 || counter2.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 567);
//                     wsc.sendMessage(message);
//                     Thread.sleep(5000);
//                     message = String.format("{\"type\": \"get_room_permits\",\"room_value\":\"567\"}");
//                     wsc.sendMessage(message);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 567);
//                     wsc.sendMessage(message);
//                     Thread.sleep(5000);
//                     message = String.format("{\"type\": \"get_room_permits\",\"room_value\":\"567\"}");
//                     wsc.sendMessage(message);
//                 }

//                 Thread.sleep(2000);
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 4; i++) {
//             execut.execute(area);
//             Thread.sleep(3500);
//         }
//         c_barrier.await();
//         Thread.sleep(3500);

//         for (int i = 0; i < 4; i++) {
//             String array_msg = array.get(i);
//             assertTrue("Game_star status " + array_msg, array_msg.contains("room_permits"));
//             System.out.println("Asserting checking values for i "+ i);
//         }

//     }


//     /**Updates back-end when the screen size change, i.e. if user expands the windows then this needs to be updated on the b/e in
//      * order to calculate the x,y move position for the game.
//      *
//      * @throws Exception
//      */
//     @Test
//     public void test_screen_size_change () throws Exception {
//         CyclicBarrier c_barrier = new CyclicBarrier(5);
//         Executor execut = Executors.newFixedThreadPool(4);



//         Runnable area = () -> {


//             WebSocketTest wsc = new WebSocketTest();

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;
//                 System.out.println("Adjusted screen size update for each player");
//                 message = "{\"type\": \"adjust_screen_size\", \"move_length\":\"50\",\"move_dev\":\"60\"}";
//                 wsc.sendMessage(message);
//                 Thread.sleep(1000);
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 4; i++) {
//             execut.execute(area);
//             Thread.sleep(2500);
//         }
//         c_barrier.await();
//         Thread.sleep(2500);


//         System.out.println("Screen size change updated for all players");
//     }

//     /**Send game finish msg to back-end to end the game
//      *
//      *
//      */
//     @Test
//     public void test_finish ()  throws Exception {
//         CyclicBarrier c_barrier = new CyclicBarrier(5);
//         Executor execut = Executors.newFixedThreadPool(4);

//         AtomicReferenceArray<String> array = new AtomicReferenceArray(4);
//         counter.set(0);


//         Runnable area = () -> {

//             int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);


//             WebSocketTest wsc = new WebSocketTest();
//             wsc.onMessage((session, message) -> {

//                 if (message.contains("game_terminated")){
//                     array.set(id, message);
//                     System.out.println("Array id " + id);
//                     System.out.println("Msg from server " + message);
//                 }
//             });

//             try {
//                 wsc.connect("ws://127.0.0.1:8080/springboot");
//                 String message;

//                 if (counter.incrementAndGet() == 1) {

//                     message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}", 888);
//                     wsc.sendMessage(message);
//                 } else if (counter.incrementAndGet() == 2 || counter.incrementAndGet() == 4) {
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 888);
//                     wsc.sendMessage(message);
//                 } else {
//                     System.out.println("Joining user of the second game");
//                     message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}", 888);
//                     wsc.sendMessage(message);
//                 }

//                 System.out.println("Game Start test for each player");
//                 Thread.sleep(2000);
//                 c_barrier.await();
//                 message = String.format("{\"type\": \"game_finish\", \"player_id\":\"%d\",\"room_value\":\"888\"}", id);
//                 wsc.sendMessage(message);
//                 Thread.sleep(1000);
//                 wsc.disconnect();
//                 c_barrier.await();

//             } catch (Exception e) {
//                 e.printStackTrace();
//                 System.out.println("Test failed");
//             }
//         };

//         for (int i = 0; i < 4; i++) {
//             execut.execute(area);
//             Thread.sleep(2500);
//         }
//         c_barrier.await();
//         Thread.sleep(2500);
//         for (int i = 0; i < 4; i++) {
//             String array_msg = array.get(i);
//             assertTrue("Game_star status " + array_msg, array_msg.contains("game_terminated"));
//             System.out.println("Asserting checking values for i "+ i);
//         }

//     }

// }
