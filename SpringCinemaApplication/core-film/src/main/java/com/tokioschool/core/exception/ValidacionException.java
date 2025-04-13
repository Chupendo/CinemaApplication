package com.tokioschool.core.exception;

import java.util.Map;

/**
 * Excepción personalizada para manejar errores de validación en la aplicación.
 * Extiende de {@link RuntimeException}, lo que permite que sea una excepción no verificada.
 *
 * Esta clase incluye un mapa de errores que asocia campos con mensajes de error específicos.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class ValidacionException extends RuntimeException {

  /**
   * Mapa que contiene los errores de validación, donde la clave es el nombre del campo
   * y el valor es el mensaje de error asociado.
   */
  private Map<String, String> errors;

  /**
   * Constructor que inicializa la excepción con un mensaje y un mapa de errores.
   *
   * @param message El mensaje descriptivo del error.
   * @param errors Un mapa que contiene los errores de validación.
   */
  public ValidacionException(String message, Map<String, String> errors) {
    super(message);
    this.errors = errors;
  }

  /**
   * Obtiene el mapa de errores de validación.
   *
   * @return Un mapa que contiene los errores de validación.
   */
  public Map<String, String> getErrors() {
    return errors;
  }

}