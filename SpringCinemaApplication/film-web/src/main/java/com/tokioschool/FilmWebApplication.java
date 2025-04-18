package com.tokioschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@EntityScan(basePackages = "com.tokioschool.filmapp.domain")
//@EnableJpaRepositories(basePackages = "com.tokioschool.filmapp.repositories")
public class FilmWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilmWebApplication.class, args);
    }
}
