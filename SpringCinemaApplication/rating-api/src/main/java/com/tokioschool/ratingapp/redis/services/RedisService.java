package com.tokioschool.ratingapp.redis.services;

/**
 * Interfaz para el servicio Redis.
 *
 * Proporciona métodos para guardar y recuperar valores en Redis utilizando claves específicas.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface RedisService {

    /**
     * Guarda un valor en Redis asociado a una clave específica.
     *
     * @param clave La clave con la que se asociará el valor.
     * @param valor El valor que se guardará en Redis.
     */
    void saveValue(String clave, String valor);

    /**
     * Recupera un valor de Redis utilizando una clave específica.
     *
     * @param clave La clave asociada al valor que se desea recuperar.
     * @return El valor asociado a la clave, o {@code null} si no existe.
     */
    String getValue(String clave);
}