package com.tokioschool.filmexport.listeners;

import com.tokioschool.filmexport.helpers.StepRegister;
import com.tokioschool.filmexport.helpers.TranslatedMessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Listener para la ejecución de un trabajo en Spring Batch.
 *
 * Este listener se ejecuta antes y después de la ejecución de un trabajo,
 * permitiendo realizar acciones como registrar mensajes de inicio y fin,
 * y generar un archivo de log con los pasos realizados durante el trabajo.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@Slf4j`: Proporciona un logger para registrar mensajes.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {
    // Constantes
    /** Clave del mensaje que define el texto inicial del archivo. */
    public static final String JOB_END_MESSAGE = "job.end.message";

    /** Clave del mensaje que define el texto final del archivo. */
    public static final String JOB_START_MESSAGE = "job.start.message";

    /** Clave del mensaje que define la ruta de salida del archivo log. */
    public static final String FILE_OUTPUT_LOG_PATH = "file.output-log.path";

    // Inyección de dependencias
    /** Componente para obtener mensajes traducidos desde un archivo de recursos. */
    private final TranslatedMessageHelper translatedMessageHelper;

    /** Componente para registrar los pasos y errores durante la ejecución del trabajo. */
    private final StepRegister stepRegister;

    /**
     * Metodo ejecutado antes de que comience el trabajo.
     *
     * Este metodo registra el ID del trabajo en el contexto de ejecución y
     * registra un mensaje de inicio en el log.
     *
     * @param jobExecution Objeto que representa la ejecución del trabajo.
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Registrar el ID del trabajo en el contexto de ejecución
        jobExecution.getExecutionContext().putLong("jobId", jobExecution.getJobId());

        // Obtener y registrar el mensaje de inicio del trabajo
        final String message = translatedMessageHelper.getMessage(
                JOB_START_MESSAGE,
                new Object[] { jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), Locale.getDefault()}
        );
        log.info("===> {}", message);
        stepRegister.addStepLog(message);
    }

    /**
     * Metodo ejecutado después de que finaliza el trabajo.
     *
     * Este metodo registra un mensaje de finalización en el log y genera un archivo
     * con el log acumulado de los pasos realizados durante el trabajo.
     *
     * @param jobExecution Objeto que representa la ejecución del trabajo.
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        // Obtener y registrar el mensaje de finalización del trabajo
        final String message = translatedMessageHelper.getMessage(
                JOB_END_MESSAGE,
                new Object[] { jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), jobExecution.getStatus() }
        );
        log.info("===> {}", message);
        stepRegister.addStepLog(message);

        try {
            // Generar el nombre del archivo de log basado en la fecha y el ID del trabajo
            final String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            final String exportFileLog = translatedMessageHelper.getMessage(
                    FILE_OUTPUT_LOG_PATH,
                    new Object[] { date, jobExecution.getJobId() }
            );

            // Escribir el log acumulado en el archivo
            Files.writeString(Paths.get(exportFileLog), stepRegister.getStepLog());
            stepRegister.clearStepLog();
        } catch (IOException e) {
            // Registrar un error si ocurre al escribir el archivo de log
            log.error("Error escribiendo el log del job", e);
        }
    }
}