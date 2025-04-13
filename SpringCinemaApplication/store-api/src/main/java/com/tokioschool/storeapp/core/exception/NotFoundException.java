package com.tokioschool.storeapp.core.exception;

/**
 * Excepción personalizada para manejar casos en los que no se encuentra un recurso.
 *
 * Esta clase extiende {@link RuntimeException} y proporciona varios constructores
 * para crear excepciones con diferentes niveles de detalle, como mensajes personalizados,
 * causas y configuraciones adicionales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructor que crea una excepción con una causa específica.
     *
     * @param cause La causa de la excepción (otra excepción que provocó esta).
     */
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor que crea una excepción con un mensaje personalizado.
     *
     * @param message El mensaje de error que describe la excepción.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor que crea una excepción con un mensaje personalizado y una causa específica.
     *
     * @param message El mensaje de error que describe la excepción.
     * @param cause   La causa de la excepción (otra excepción que provocó esta).
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor que crea una excepción con un mensaje personalizado, una causa específica,
     * y configuraciones adicionales para la supresión y la trazabilidad del stack.
     *
     * @param message            El mensaje de error que describe la excepción.
     * @param cause              La causa de la excepción (otra excepción que provocó esta).
     * @param enableSuppression  Indica si la supresión está habilitada o no.
     * @param writableStackTrace Indica si la traza del stack es escribible o no.
     */
    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}