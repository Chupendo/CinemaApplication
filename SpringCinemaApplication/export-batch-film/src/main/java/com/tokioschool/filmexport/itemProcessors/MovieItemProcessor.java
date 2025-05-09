package com.tokioschool.filmexport.itemProcessors;

import com.tokioschool.filmapp.domain.ExportFilm;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmexport.helpers.StepRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Procesador de elementos para transformar objetos de tipo `Movie` en objetos de tipo `ExportFilm`.
 *
 * Esta clase implementa la interfaz `ItemProcessor` de Spring Batch, permitiendo procesar
 * cada elemento de entrada (`Movie`) y transformarlo en un elemento de salida (`ExportFilm`).
 *
 * Anotaciones utilizadas:
 * - `@StepScope`: Define el alcance del bean como específico para un paso de Spring Batch.
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@StepScope
@Component
@RequiredArgsConstructor
public class MovieItemProcessor implements ItemProcessor<Movie, ExportFilm> {

    /** Registro de pasos y errores durante la ejecución del proceso. */
    private final StepRegister stepRegister;

    /**
     * ID del trabajo actual, obtenido del contexto de ejecución del paso.
     * Utiliza SpEL (Spring Expression Language) para acceder al ID del trabajo.
     */
    @Value("#{stepExecution.jobExecution.id}")
    private Long jobId;

    /**
     * Procesa un objeto de tipo `Movie` y lo transforma en un objeto de tipo `ExportFilm`.
     *
     * @param movie Objeto de entrada de tipo `Movie` que será procesado.
     * @return Objeto de salida de tipo `ExportFilm` con los datos transformados.
     * @throws Exception Si ocurre algún error durante el procesamiento.
     */
    @Override
    public ExportFilm process(@NonNull Movie movie) throws Exception {
        // Registrar el paso actual en el log
        stepRegister.addStepLog("Processing movie: " + movie.getTitle());

        // Construir y devolver el objeto ExportFilm
        return ExportFilm.builder()
                .jobId(jobId)
                .movie(movie)
                .build();
    }
}