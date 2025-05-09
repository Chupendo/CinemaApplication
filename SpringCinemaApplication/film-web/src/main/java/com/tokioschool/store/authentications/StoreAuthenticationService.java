package com.tokioschool.store.authentications;

/**
 * Interfaz para el servicio de autenticación de la tienda.
 *
 * Esta interfaz define los métodos necesarios para obtener un token de acceso,
 * ya sea utilizando un nombre de usuario predeterminado o uno específico.
 *
 * @author andres.rpenulea
 * @version 1.0
 */
public interface StoreAuthenticationService {

    /**
     * Obtiene el token de acceso utilizando el nombre de usuario predeterminado.
     *
     * @return El token de acceso actual o un nuevo token si el actual ha expirado.
     */
    String getAccessToken();

    /**
     * Obtiene el token de acceso para un usuario específico.
     *
     * @param userName El nombre de usuario para el cual se solicita el token.
     * @return El token de acceso actual o un nuevo token si el actual ha expirado.
     */
    String getAccessToken(String userName);
}