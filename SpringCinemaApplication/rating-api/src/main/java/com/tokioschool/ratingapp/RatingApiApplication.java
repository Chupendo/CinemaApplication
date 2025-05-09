package com.tokioschool.ratingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación Rating API.
 *
 * Esta clase configura y lanza la aplicación Spring Boot. Incluye configuraciones
 * para escaneo de componentes, entidades y repositorios JPA.
 *
 * Anotaciones utilizadas:
 * - `@SpringBootApplication`: Marca esta clase como la principal para iniciar la aplicación Spring Boot.
 * - `@ComponentScan`: Especifica los paquetes base para escanear componentes Spring.
 * - `@EntityScan`: Define los paquetes donde se encuentran las entidades JPA.
 * - `@EnableJpaRepositories`: Habilita los repositorios JPA en los paquetes especificados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.tokioschool"})
@EntityScan(basePackages = "com.tokioschool.filmapp.domain")
@EnableJpaRepositories(basePackages = "com.tokioschool.filmapp.repositories")
public class RatingApiApplication {

    /**
     * Metodo principal para iniciar la aplicación.
     *
     * Este metodo utiliza `SpringApplication.run` para lanzar la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(RatingApiApplication.class, args);
    }
}