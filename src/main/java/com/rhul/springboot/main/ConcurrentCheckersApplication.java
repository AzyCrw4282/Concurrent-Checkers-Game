package com.rhul.springboot.main;

import com.rhul.springboot.controller.CheckersHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**Main runnable class.
 * @author Azky
 */
@RestController
@Controller
@EnableWebSocket
@SpringBootApplication
public class ConcurrentCheckersApplication implements WebSocketConfigurer {


    public static void main(String[] args) {
        SpringApplication.run(ConcurrentCheckersApplication.class, args);
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