package com.tokioschool.filmexport.readers;

import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmapp.repositories.MovieDao;
import com.tokioschool.filmexport.helpers.StepRegister;
import com.tokioschool.filmexport.helpers.TranslatedMessageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

/**
 * Lector de elementos para procesar películas no exportadas.
 *
 * Esta clase implementa la interfaz `ItemReader` de Spring Batch para leer
 * objetos de tipo `Movie` desde un repositorio. También implementa
 * `StepExecutionListener` para realizar acciones antes y después de la ejecución
 * de un paso.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@Slf4j`: Proporciona un logger para registrar mensajes.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos para las dependencias finales.
 * - `@StepScope`: Define el alcance del bean como específico para un paso de Spring Batch.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
@StepScope
public class MovieItemReader implements ItemReader<Movie>, StepExecutionListener {

    /** Clave del mensaje que indica el tamaño del lote exportado. */
    public static final String FILM_EXPORTED_SIZE = "film.exported.size";

    /** Clave del mensaje que indica un error al leer películas. */
    public static final String FILM_EXPORTED_READ_ERROR = "film.exported.read.error";

    /** DAO para acceder a las películas desde el repositorio. */
    private final MovieDao movieDao;

    /** Registro de pasos y errores durante la ejecución del proceso. */
    private final StepRegister stepRegister;

    /** Iterador para recorrer el lote actual de películas. */
    private Iterator<Movie> currentBatchIterator;

    /** Ejecución del paso actual. */
    private StepExecution stepExecution;

    /** Componente para obtener mensajes traducidos desde un archivo de recursos. */
    private final TranslatedMessageHelper translatedMessageHelper;

    /**
     * Metodo  ejecutado antes de que comience el paso.
     *
     * @param stepExecution Objeto que representa la ejecución del paso.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    /**
     * Lee y devuelve el siguiente objeto `Movie` del lote actual.
     *
     * Si el lote actual ha sido procesado completamente, se carga un nuevo lote
     * desde el repositorio. En caso de error, se registra el error y se marca
     * el paso como fallido.
     *
     * @return El siguiente objeto `Movie` o `null` si no hay más elementos.
     * @throws Exception Si ocurre algún error durante la lectura.
     */
    @Override
    public Movie read() throws Exception {
        try {
            // Solo cargamos un nuevo lote de películas si el actual ha sido procesado completamente
            if (currentBatchIterator == null || !currentBatchIterator.hasNext()) {
                // Leer el siguiente lote de películas desde el repositorio
                final List<Movie> movies = movieDao.findFilmsNotExported(); // Cargar el próximo lote
                final String message = translatedMessageHelper.getMessage(FILM_EXPORTED_SIZE, new Object[]{movies.size()});
                stepRegister.addStepLog(message);  // Registrar el tamaño del lote
                currentBatchIterator = movies.iterator();
            }

            // Devolver la siguiente película del lote actual
            return currentBatchIterator != null && currentBatchIterator.hasNext() ? currentBatchIterator.next() : null;
        } catch (Exception e) {
            // Registrar el error
            final String message = translatedMessageHelper.getMessage(FILM_EXPORTED_READ_ERROR, new Object[]{e.getMessage()});
            log.error(message, e);
            stepRegister.addStepErrorLog(message, e);

            // Manejo de errores
            stepExecution.setStatus(BatchStatus.FAILED);
            stepExecution.setExitStatus(new ExitStatus("ERROR", e.getMessage()));

            return null;
        }
    }

    /**
     * Metodo  ejecutado después de que finaliza el paso.
     *
     * @param stepExecution Objeto que representa la ejecución del paso.
     * @return El estado de salida del paso.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}