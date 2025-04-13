package com.tokioschool.ratingapp.securities.routes;

/**
 * Interfaz que define las rutas utilizadas en la configuración de seguridad de la aplicación.
 *
 * Proporciona constantes para las rutas que se utilizan en la configuración de seguridad,
 * como la consola H2, el inicio de sesión y las rutas de lista blanca.
 * Estas constantes ayudan a centralizar y reutilizar las rutas en el proyecto.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface Routes {

    /**
     * Ruta completa para acceder a la consola H2.
     */
    String H2_CONSOLE_FULL = "/h2-console/**";

    /**
     * Ruta completa para la API de inicio de sesión.
     */
    String LOGIN_API_FULL = "/api/ratings/login";

    /**
     * Rutas permitidas sin autenticación.
     *
     * Incluye rutas como el inicio de sesión, errores, autenticación OAuth2,
     * y la consola H2.
     */
    String[] WHITE_LIST_URLS = {
            "/login**",
            "/error",
            "/oauth2/authenticate",
            "/login/oauth2/code/**",
            "/oauth2/secret",
            "/.well-known/**",
            H2_CONSOLE_FULL,
            LOGIN_API_FULL
    };
}