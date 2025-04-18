package com.tokioschool.filmexport.helpers;

import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Componente para registrar los pasos y errores durante la ejecución de un proceso.
 *
 * Esta clase permite agregar mensajes de log relacionados con los pasos y errores
 * ocurridos en un proceso, así como obtener y limpiar el log acumulado.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
public class StepRegister {

    /** Almacena los mensajes de log de los pasos y errores. */
    private StringBuilder logSteps = new StringBuilder();

    /**
     * Agrega un mensaje al log relacionado con un paso del proceso.
     *
     * @param message Mensaje descriptivo del paso.
     */
    public void addStepLog(String message) {
        logSteps.append("[Paso] ").append(message).append("\n");
    }

    /**
     * Agrega un mensaje de error al log, incluyendo la traza completa de la excepción.
     *
     * @param message Mensaje descriptivo del error.
     * @param e Excepción que causó el error.
     */
    public void addStepErrorLog(String message, Exception e) {
        logSteps.append("[Error] ").append(message).append(": ").append(e.getMessage()).append("\n");

        // Captura la traza completa de la excepción
        final StringWriter sw = new StringWriter();

        // debug
        //PrintWriter pw = new PrintWriter(sw);
        //e.printStackTrace( pw );

        // Añade la traza completa al log
        logSteps.append( sw ).append("\n");
    }

    /**
     * Obtiene el log acumulado de los pasos y errores.
     *
     * @return Log acumulado como una cadena de texto.
     */
    public String getStepLog() {
        return logSteps.toString();
    }

    /**
     * Limpia el log acumulado de los pasos y errores.
     */
    public void clearStepLog() {
        logSteps = new StringBuilder();
    }
}