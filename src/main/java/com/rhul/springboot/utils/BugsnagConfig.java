package com.rhul.springboot.utils;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BugsnagSpringConfiguration.class)
public class BugsnagConfig {
    @Bean
    public static Bugsnag bugsnag() {
        return new Bugsnag("58b3b400437ffde6119c14c6f0b358a8");
    }


}
