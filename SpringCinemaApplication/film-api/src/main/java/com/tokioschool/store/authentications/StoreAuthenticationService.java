package com.tokioschool.store.authentications;

/**
 * Interfaz para el servicio de autenticación de la tienda.
 *
 * Proporciona métodos para obtener un token de acceso, ya sea utilizando un nombre de usuario
 * predeterminado o uno específico.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface StoreAuthenticationService {

    /**
     * Obtiene un token de acceso utilizando el nombre de usuario predeterminado.
     *
     * @return El token de acceso.
     */
    String getAccessToken();

    /**
     * Obtiene un token de acceso para un nombre de usuario específico.
     *
     * @param userName El nombre de usuario para el cual se obtendrá el token de acceso.
     * @return El token de acceso.
     */
    String getAccessToken(String userName);

}