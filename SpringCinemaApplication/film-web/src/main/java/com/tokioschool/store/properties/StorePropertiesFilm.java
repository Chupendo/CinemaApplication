package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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