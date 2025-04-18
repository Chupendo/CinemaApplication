package com.tokioschool.filmexport.configs.batchs;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de procesamiento por lotes para el perfil de prueba.
 *
 * Esta clase habilita el procesamiento por lotes de Spring Batch cuando se utiliza
 * el perfil "test". La anotación `@EnableBatchProcessing` permite configurar y
 * ejecutar trabajos por lotes en la aplicación.
 *
 * Anotaciones utilizadas:
 * - `@Profile("test")`: Indica que esta configuración solo se aplica cuando el
 *   perfil activo es "test".
 * - `@EnableBatchProcessing`: Habilita la funcionalidad de procesamiento por lotes
 *   de Spring Batch.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Profile("test")
@EnableBatchProcessing // Si se habilita, las tablas no se crean automáticamente. Si se elimina, las tablas se crean.
public class BatchProcessingConfig {
}