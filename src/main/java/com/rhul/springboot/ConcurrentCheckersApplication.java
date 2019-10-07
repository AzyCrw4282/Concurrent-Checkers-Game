package com.rhul.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//Main runnable class.
@SpringBootApplication
public class ConcurrentCheckersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcurrentCheckersApplication.class, args);
    }

}
