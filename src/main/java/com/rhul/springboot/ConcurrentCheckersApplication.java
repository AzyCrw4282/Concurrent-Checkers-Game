package com.rhul.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//Main runnable class.
@RestController
@EnableWebSocket
@SpringBootApplication
public class ConcurrentCheckersApplication implements WebSocketConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(ConcurrentCheckersApplication.class, args);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHello(){
        return "test success";
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(checkers_handler(), "/springboot");
    }

    @Bean
    public WebSocketHandler checkers_handler() {
        return new CheckersHandler();
    }
}