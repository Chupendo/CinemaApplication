package com.tokioschool.filmapp.repositories.configuration;

import com.github.javafaker.Faker;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

/**
 * Class with configuration for testing the repositories
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
@Profile("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MariaDB;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@EnableJpaRepositories(basePackages = "com.tokioschool.filmapp.repositories")
@EntityScan(basePackages = {"com.tokioschool.filmapp.domain"})
public class TestConfig {

    @Bean
    public Faker faker(){
        return new Faker();
    }
}
