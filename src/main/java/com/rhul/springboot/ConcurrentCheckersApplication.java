package com.rhul.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//Main runnable class.
@RestController
@SpringBootApplication
public class ConcurrentCheckersApplication {

    @RequestMapping(value = "/sayHello", method = RequestMethod.GET)
    public String sayHello(){
        return "Hello boys";
    }


    public static void main(String[] args) {
        SpringApplication.run(ConcurrentCheckersApplication.class, args);
    }
}
