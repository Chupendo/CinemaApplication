package com.tokioschool.filmexport.writers;

import com.tokioschool.filmapp.domain.ExportFilm;
import com.tokioschool.filmapp.repositories.ExportFilmDao;
import com.tokioschool.filmexport.helpers.FilePathProvider;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Escritor de elementos para exportar objetos de tipo `ExportFilm` a un archivo plano (CSV).
 *
 * Esta clase extiende `FlatFileItemWriter` de Spring Batch y también implementa
 * `StepExecutionListener` para realizar acciones antes y después de la ejecución del paso.
 *
 * Anotaciones utilizadas:
 * - `@Component`: Marca esta clase como un componente Spring para la inyección de dependencias.
 * - `@StepScope`: Define el alcance del bean como específico para un paso de Spring Batch.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Component
@StepScope
public class MovieItemWriter extends FlatFileItemWriter<ExportFilm> implements StepExecutionListener {

    /** DAO para acceder y guardar los objetos `ExportFilm` en la base de datos. */
    private final ExportFilmDao exportFilmDao;

    /** ID de la ejecución del trabajo, inyectado desde el contexto de ejecución del paso. */
    @Value("#{stepExecution.jobExecution.id}")
    private Long jobExecutionId;

    /** Proveedor de rutas de archivo para construir el nombre y encabezado del archivo de salida. */
    private final FilePathProvider filePathProvider;

    /**
     * Constructor que inicializa las dependencias y configura el escritor de archivos.
     *
     * @param exportFilmDao DAO para la gestión de los objetos `ExportFilm`.
     * @param filePathProvider Proveedor de rutas de archivo para el archivo de salida.
     */
    public MovieItemWriter(ExportFilmDao exportFilmDao, FilePathProvider filePathProvider) {
        this.exportFilmDao = exportFilmDao;
        this.filePathProvider = filePathProvider;

        // Configuración inicial del escritor
        this.setAppendAllowed(false); // No permite agregar contenido al archivo existente
        this.setShouldDeleteIfEmpty(true); // Elimina el archivo si está vacío
        this.setEncoding(StandardCharsets.UTF_8.name()); // Configura la codificación del archivo

        // Configuración del encabezado del archivo
        this.setHeaderCallback(writer -> writer.write(filePathProvider.buildHeader()));

        // Configuración del agregador de líneas para escribir las líneas del archivo CSV
        this.setLineAggregator(new DelimitedLineAggregator<>() {{
            setDelimiter(","); // Delimitador de campos
            setFieldExtractor(exportFilm -> new Object[]{
                    exportFilm.getMovie().getId(),
                    exportFilm.getMovie().getTitle(),
                    exportFilm.getMovie().getReleaseYear()
            });
        }});
    }

    /**
     * Escribe un conjunto de objetos `ExportFilm` en el archivo de salida.
     *
     * @param items Conjunto de elementos a escribir.
     * @throws Exception Si ocurre algún error durante la escritura.
     */
    @Override
    public void write(Chunk<? extends ExportFilm> items) throws Exception {
        super.write(items);
    }

    /**
     * Abre el recurso de archivo antes de comenzar a escribir.
     *
     * @param executionContext Contexto de ejecución del paso.
     */
    @Override
    public void open(org.springframework.batch.item.ExecutionContext executionContext) {
        // Construir el nombre del archivo de salida basado en el ID de la ejecución del trabajo
        final String fileName = filePathProvider.getOutputFileName(jobExecutionId);
        this.setResource(new FileSystemResource(fileName));
        super.open(executionContext);
    }

    /**
     * Metodo  ejecutado después de que finaliza el paso.
     *
     * @param stepExecution Objeto que representa la ejecución del paso.
     * @return El estado de salida del paso.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}