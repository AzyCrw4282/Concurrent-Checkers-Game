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

import java.net.URI;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentCheckersApplicationTests {

    private AtomicInteger counter  = new AtomicInteger(1);

    @BeforeClass
    public static void start_server() {
        ConcurrentCheckersApplication.main(new String[] {"--server.port=8080"});
    }
    /**
     *
     * @throws Exception
     */
    @Test
    public void test_connection() throws Exception{
        System.out.println("----------------------------Testing connection-------------------");
        WebSocketTest Wsc = new WebSocketTest();
        Wsc.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
        Wsc.disconnect();
    }

    /** This test is used to check joining upto 4.
     *
     * @throws Exception
     */
    @Test
    public void test_join() throws Exception{
        //steps - > connect, if coutner firstr then create room requ. else join req
        //send the msg of
        //and then finish request
        //conenction disconnet
        counter.set(1);
        CyclicBarrier c_barrier = new CyclicBarrier(5);
        Executor execut = Executors.newFixedThreadPool(4);

        AtomicReferenceArray array = new AtomicReferenceArray(4);

        Runnable area = () -> {

            //for each runnable thread we get the running id of it
            int id = Character.getNumericValue(Thread.currentThread().getName().charAt(Thread.currentThread().getName().length() - 1) - 1);
            WebSocketTest ws = new WebSocketTest();

            try{
                ws.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
                String message;

                //perform exact ops of initiz and joining methods
                if (counter.get() == 1){
                    //value hard_coded for the purpose fo testing
                    message = String.format("{\"type\": \"user\", \"room_action\":\"create_room\",\"room_value\":\"%d\"}",counter.getAndIncrement());
                }
                else if (counter.get()  == 2 || counter.get() == 4){//joining users opp, automatically initz game
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}",counter.getAndIncrement());
                }
                else if (counter.get() == 3){//joinig user of second game
                    message = String.format("{\"type\": \"user\", \"room_action\":\"join_room\",\"room_value\":\"%d\"}",counter.getAndIncrement());
                }
                else {
                    message = "problem occured";
                }
                ws.sendMessage(message);
                Thread.sleep(2000);
                message = "{\"type\": \"game_finish\"}";
                ws.sendMessage(message);
                ws.disconnect();
                c_barrier.await();


                //msg from back-end, i.e. responses
                ws.onMessage((session,msg)->{
                    System.out.println("Msg from back-end "+ msg);
                });


            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Test failed");
            }
        };
        for ( int i=0;i<4;i++){
            execut.execute(area);
            Thread.sleep(1500);
        }
        c_barrier.await();

    }
    /**
     *
     * @throws Exception
     */
    @Test
    public void test_finish()
    {


    }
    /**
     *
     * @throws Exception
     */
    @Test
    public void test_wait()
    {


    }

}
