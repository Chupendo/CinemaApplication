package com.tokioschool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación FilmWeb.
 *
 * Esta clase inicializa y ejecuta la aplicación Spring Boot.
 *
 * Anotaciones:
 * - {@link SpringBootApplication}: Marca esta clase como la configuración principal de Spring Boot.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@SpringBootApplication
public class FilmWebApplication {

    /**
     * Metodo principal que sirve como punto de entrada para la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(FilmWebApplication.class, args);
    }
}