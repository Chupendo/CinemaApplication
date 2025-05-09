package com.tokioschool.storeapp.configuration.properties;

import com.tokioschool.storeapp.core.helper.FileHelper;
import jakarta.annotation.Nonnull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * Clase de configuración de propiedades para la tienda.
 *
 * Esta clase utiliza la anotación {@link ConfigurationProperties} para mapear las propiedades
 * definidas en el archivo de configuración de la aplicación con el prefijo "application.store".
 * Proporciona métodos para construir rutas de recursos basadas en las propiedades configuradas.
 *
 * @param absolutePath Ruta absoluta configurada en las propiedades.
 * @param relativePath Ruta relativa configurada en las propiedades.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@ConfigurationProperties(prefix = "application.store")
public record StoreConfigurationProperties(Path absolutePath, String relativePath) {

    /**
     * Construye la ruta de recursos a partir de la ruta relativa configurada en las propiedades.
     *
     * @return La ruta del recurso como un objeto {@link Path}.
     * @see FileHelper#getCurrentWorking(String)
     */
    public Path buildResourcePathFromRelativePathGivenNameResource() {
        return FileHelper.getCurrentWorking(relativePath);
    }

    /**
     * Construye la ruta de recursos a partir de la ruta absoluta configurada en las propiedades.
     *
     * @return La ruta del recurso como un objeto {@link Path}.
     */
    public Path buildResourcePathFromAbsolutePath() {
        return absolutePath;
    }

    /**
     * Construye la ruta de un recurso específico dado su nombre, basado en la ruta relativa configurada.
     *
     * @param nameResource Nombre del recurso para obtener su ruta.
     * @return La ruta del recurso como un objeto {@link Path}.
     * @see FileHelper#getCurrentWorking(String)
     *
     * Nota: Otra opción sería:
     * return Path.of(getResourcePathFromRelativePathGivenNameResource().toString(), nameResource);
     */
    public Path getResourcePathFromRelativePathGivenNameResource(@Nonnull String nameResource) {
        return buildResourcePathFromRelativePathGivenNameResource().resolve(nameResource);
    }
}