package com.tokioschool.filmexport.configs.batchs;

import com.tokioschool.filmapp.domain.ExportFilm;
import com.tokioschool.filmapp.domain.Movie;
import com.tokioschool.filmexport.itemProcessors.MovieItemProcessor;
import com.tokioschool.filmexport.listeners.JobCompletionNotificationListener;
import com.tokioschool.filmexport.readers.MovieItemReader;
import com.tokioschool.filmexport.writers.ExportFilmDbWriter;
import com.tokioschool.filmexport.writers.MovieItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Locale;

/**
 * Configuración de Spring Batch para la exportación de películas.
 *
 * Esta clase define los beans necesarios para configurar un trabajo de Spring Batch
 * que procesa y exporta películas. Incluye la definición de un `Job`, un `Step` y
 * un escritor compuesto para manejar múltiples destinos de escritura.
 *
 * Anotaciones utilizadas:
 * - `@Configuration`: Marca esta clase como una clase de configuración de Spring.
 * - `@EnableAutoConfiguration`: Habilita la configuración automática de Spring Boot.
 * - `@RequiredArgsConstructor`: Genera un constructor con los argumentos requeridos.
 *
 * @author andres.rpeneula
 * @version 1.0
 */
@Configuration
@EnableAutoConfiguration
@RequiredArgsConstructor
public class BathConfig {

    /** Listener para manejar eventos al completar el trabajo. */
    private final JobCompletionNotificationListener jobCompletionNotificationListener;

    /** Idioma de la aplicación, configurado a través de propiedades. */
    @Value("${spring.application.language.en:: es_Es}")
    private String language;

    /**
     * Define el trabajo de exportación de películas.
     *
     * @param jobRepository Repositorio de trabajos de Spring Batch.
     * @param transactionManager Administrador de transacciones.
     * @param movieItemReader Lector de elementos para leer películas.
     * @param movieItemProcessor Procesador de elementos para procesar películas.
     * @param movieItemWriter Escritor de elementos para escribir películas.
     * @param exportFilmDbWriter Escritor de elementos para guardar exportaciones en la base de datos.
     * @return El trabajo configurado.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public Job job(JobRepository jobRepository,
                   PlatformTransactionManager transactionManager,
                   MovieItemReader movieItemReader,
                   MovieItemProcessor movieItemProcessor,
                   MovieItemWriter movieItemWriter,
                   ExportFilmDbWriter exportFilmDbWriter) throws Exception {

        // Configura el idioma predeterminado de la aplicación
        Locale.setDefault(Locale.of(language));

        return new JobBuilder("movieExportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobCompletionNotificationListener)
                .start(chunkStep(jobRepository, transactionManager, movieItemReader, movieItemProcessor, movieItemWriter, exportFilmDbWriter))
                .build();
    }

    /**
     * Define un paso del trabajo que procesa y exporta películas.
     *
     * @param jobRepository Repositorio de trabajos de Spring Batch.
     * @param transactionManager Administrador de transacciones.
     * @param movieItemReader Lector de elementos para leer películas.
     * @param movieItemProcessor Procesador de elementos para procesar películas.
     * @param movieItemWriter Escritor de elementos para escribir películas.
     * @param exportFilmDbWriter Escritor de elementos para guardar exportaciones en la base de datos.
     * @return El paso configurado.
     */
    @Bean
    public Step chunkStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          MovieItemReader movieItemReader,
                          MovieItemProcessor movieItemProcessor,
                          MovieItemWriter movieItemWriter,
                          ExportFilmDbWriter exportFilmDbWriter) {

        return new StepBuilder("step", jobRepository).<Movie, ExportFilm>chunk(1, transactionManager)
                .reader(movieItemReader)
                .processor(movieItemProcessor)
                .writer(compositeItemWriter(movieItemWriter, exportFilmDbWriter))
                .build();
    }

    /**
     * Define un escritor compuesto que delega la escritura a múltiples escritores.
     *
     * @param movieItemWriter Escritor de elementos para escribir películas.
     * @param exportFilmDbWriter Escritor de elementos para guardar exportaciones en la base de datos.
     * @return El escritor compuesto configurado.
     */
    @Bean
    @StepScope
    public CompositeItemWriter<ExportFilm> compositeItemWriter(MovieItemWriter movieItemWriter,
                                                               ExportFilmDbWriter exportFilmDbWriter) {
        CompositeItemWriter<ExportFilm> writer = new CompositeItemWriter<>();
        writer.setDelegates(Arrays.asList(exportFilmDbWriter, movieItemWriter));
        return writer;
    }
}