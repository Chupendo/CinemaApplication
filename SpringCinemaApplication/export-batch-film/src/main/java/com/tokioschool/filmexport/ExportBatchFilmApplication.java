package com.tokioschool.filmexport;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación Spring Boot para la exportación de películas.
 *
 * Esta clase configura y ejecuta la aplicación, habilitando el procesamiento por lotes
 * y la integración con JPA para la gestión de entidades y repositorios.
 *
 * Anotaciones utilizadas:
 * - `@SpringBootApplication`: Marca esta clase como la entrada principal de la aplicación Spring Boot.
 * - `@ComponentScan`: Especifica los paquetes base para escanear componentes Spring.
 * - `@EntityScan`: Define los paquetes donde se encuentran las entidades JPA.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 *
 * @author andres.rpenuela
 * @version andres.rpenuela
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.tokioschool"})
@EntityScan(basePackages = "com.tokioschool.filmapp.domain")
@EnableJpaRepositories(basePackages = "com.tokioschool.filmapp.repositories")
@RequiredArgsConstructor
public class ExportBatchFilmApplication {

    /**
     * Metodo principal que inicia la aplicación Spring Boot.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {

        // Ejecuta la aplicación y finaliza con un código de salida.
        System.exit(SpringApplication.exit(SpringApplication.run(ExportBatchFilmApplication.class, args)));
        // Alternativamente, se puede usar la línea comentada para iniciar sin finalizar.
        // SpringApplication.run(ExportBatchFilmApplication.class, args);
    }
}