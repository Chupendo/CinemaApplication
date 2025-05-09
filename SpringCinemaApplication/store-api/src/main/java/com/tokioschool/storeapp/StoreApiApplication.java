package com.tokioschool.storeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Store API.
 *
 * Esta clase inicializa y ejecuta la aplicación Spring Boot para la API de la tienda.
 * Contiene el metodo `main` que actúa como punto de entrada de la aplicación.
 *
 * @version andres.rpenuela
 * @version 1.0
 */
@SpringBootApplication
public class StoreApiApplication {

    /**
     * metodo principal que inicia la aplicación.
     *
     * Este metodo utiliza `SpringApplication.run` para lanzar el contexto de Spring Boot
     * y poner en marcha la aplicación.
     *
     * @param args Argumentos de línea de comandos pasados al iniciar la aplicación.
     */
    public static void main(String[] args) {
        SpringApplication.run(StoreApiApplication.class, args);
    }

}