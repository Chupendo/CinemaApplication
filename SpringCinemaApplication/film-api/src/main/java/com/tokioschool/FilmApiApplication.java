package com.tokioschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación Film API.
 *
 * Esta clase inicializa y ejecuta la aplicación Spring Boot.
 *
 * Anotaciones:
 * - {@link SpringBootApplication}: Marca esta clase como la configuración principal de Spring Boot.
 * - {@link ComponentScan}: Especifica los paquetes base para escanear componentes de Spring.
 * - {@link EntityScan}: Define los paquetes base para escanear entidades JPA.
 * - {@link EnableJpaRepositories}: Habilita la detección de repositorios JPA en los paquetes especificados.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@SpringBootApplication
///@ComponentScan(basePackages = {"com.tokioschool"})
@EntityScan(basePackages = "com.tokioschool.filmapp.domain")
@EnableJpaRepositories(basePackages = "com.tokioschool.filmapp.repositories")
public class FilmApiApplication {

    /**
     * Método principal que inicia la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(FilmApiApplication.class, args);
    }
}