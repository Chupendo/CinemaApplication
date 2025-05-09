package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Clase de configuración de propiedades para la tienda de películas.
 *
 * Esta clase utiliza el prefijo "application.store" para mapear las propiedades
 * definidas en los archivos de configuración de la aplicación (por ejemplo, application.yml o application.properties).
 *
 * Campos:
 * - {@code baseUrl}: URL base para acceder a los recursos de la tienda.
 * - {@code login}: Configuración de inicio de sesión que incluye una lista de usuarios y sus credenciales.
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