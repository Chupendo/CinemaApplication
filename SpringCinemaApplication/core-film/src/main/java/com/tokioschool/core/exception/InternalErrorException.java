package com.tokioschool.core.exception;

/**
 * Excepción personalizada para representar errores internos en la aplicación.
 * Extiende de {@link RuntimeException}, lo que permite que sea una excepción no verificada.
 *
 * Esta clase proporciona múltiples constructores para diferentes escenarios de manejo de excepciones.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class InternalErrorException extends RuntimeException {

  /**
   * Constructor que acepta un mensaje de error.
   *
   * @param message El mensaje descriptivo del error.
   */
  public InternalErrorException(String message) {
    super(message);
  }

  /**
   * Constructor que acepta una causa del error.
   *
   * @param cause La causa original de la excepción.
   */
  public InternalErrorException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor que acepta un mensaje de error y una causa.
   *
   * @param message El mensaje descriptivo del error.
   * @param cause La causa original de la excepción.
   */
  public InternalErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor que permite configurar la supresión y la capacidad de escritura del stack trace.
   *
   * @param message El mensaje descriptivo del error.
   * @param cause La causa original de la excepción.
   * @param enableSuppression Indica si la supresión está habilitada o no.
   * @param writableStackTrace Indica si el stack trace es escribible o no.
   */
  public InternalErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}