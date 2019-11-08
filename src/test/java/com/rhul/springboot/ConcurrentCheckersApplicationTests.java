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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConcurrentCheckersApplicationTests {

    @BeforeClass
    public static void start_server() {
        ConcurrentCheckersApplication.main(new String[] {"--server.port=8080"});
    }

    @Test
    public void test_connection() throws Exception{
        System.out.println("----------------------------Testing connection-------------------");
        WebSocketTest Wsc = new WebSocketTest();
        Wsc.connect("ws://127.0.0.1:8080/springboot");//specifies a handler at given url
        Wsc.disconnect();
    }

    @Test
    public void test_join() throws Exception{



    }
    @Test
    public void test_finish()
    {


    }

    @Test
    public void test_wait()
    {


    }




}
