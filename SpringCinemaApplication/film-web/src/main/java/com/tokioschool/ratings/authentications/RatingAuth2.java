package com.tokioschool.ratings.authentications;

/**
 * Interfaz para la autenticación y manejo de tokens de acceso en el sistema de calificaciones.
 *
 * Esta interfaz define un contrato para obtener el token de acceso necesario
 * para autenticar solicitudes relacionadas con las calificaciones.
 * 
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RatingAuth2 {

    /**
     * Obtiene el token de acceso.
     *
     * Este metodo debe ser implementado para proporcionar el token de acceso
     * utilizado en la autenticación de solicitudes.
     *
     * @return Una cadena que representa el token de acceso.
     */
    String getTokenAccess();
}