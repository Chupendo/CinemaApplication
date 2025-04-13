package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Clase de propiedades para la configuración de la tienda de películas.
 *
 * Esta clase utiliza el prefijo "application.store" para mapear las propiedades
 * definidas en el archivo de configuración de la aplicación.
 *
 * Anotaciones:
 * - {@link ConfigurationProperties}: Indica que esta clase contiene propiedades
 *   configurables que se cargan desde el archivo de configuración de Spring Boot.
 *
 * Campos:
 * - {@code baseUrl}: URL base para interactuar con la tienda.
 * - {@code login}: Configuración de inicio de sesión que incluye una lista de usuarios.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ConfigurationProperties(prefix = "application.store")
public record StorePropertiesFilm(String baseUrl, Login login) {

    /**
     * Clase interna que representa las credenciales de un usuario de la tienda.
     *
     * Campos:
     * - {@code username}: Nombre de usuario.
     * - {@code password}: Contraseña del usuario.
     */
    public record UserStore(String username, String password) {}

    /**
     * Clase interna que representa la configuración de inicio de sesión.
     *
     * Campos:
     * - {@code users}: Lista de usuarios con sus credenciales.
     */
    public record Login(List<UserStore> users) {}

}