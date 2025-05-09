package com.tokioschool.securities.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase de configuración para habilitar CORS (Cross-Origin Resource Sharing) en la aplicación.
 *
 * Esta clase implementa la interfaz `WebMvcConfigurer` para personalizar el comportamiento
 * de CORS en las rutas específicas de la aplicación.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    /**
     * Configura los mapeos de CORS para las rutas de la aplicación.
     *
     * Este metodo define las reglas de CORS para permitir solicitudes POST y GET
     * desde un origen específico (`http://localhost:8083`) hacia las rutas configuradas.
     *
     * @param registry El registro de CORS donde se definen las configuraciones.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuración para las rutas bajo "/web/**"
        // Permite solo solicitudes POST y GET desde el entorno local.
        registry.addMapping("/web/**")
                .allowedMethods(HttpMethod.POST.name(), HttpMethod.GET.name())
                .allowedOrigins("http://localhost:8083");

        // Configuración para la ruta "/login"
        // Aplica las mismas reglas que las rutas bajo "/web/**".
        registry.addMapping("/login")
                .allowedMethods(HttpMethod.POST.name(), HttpMethod.GET.name())
                .allowedOrigins("http://localhost:8083");
    }
}