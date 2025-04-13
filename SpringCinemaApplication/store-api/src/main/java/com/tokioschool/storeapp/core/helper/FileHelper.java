package com.tokioschool.storeapp.core.helper;

import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Clase de utilidad para operaciones relacionadas con archivos y directorios.
 *
 * Esta clase proporciona métodos estáticos para manejar rutas, crear directorios
 * y eliminar archivos o directorios de manera segura.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public class FileHelper {

    // Constructor privado para evitar la instanciación de la clase.
    private FileHelper(){}

    /**
     * Obtiene la ruta actual de trabajo con un segmento adicional opcional.
     *
     * @param segmentPath Fragmento opcional de la ruta que se añadirá al directorio de trabajo actual.
     * @return La ruta completa que incluye el segmento proporcionado, o una ruta vacía si el segmento es nulo.
     */
    public static Path getCurrentWorking(@Nullable String segmentPath){
        return Optional.ofNullable(segmentPath)
                .map(FileSystems.getDefault()::getPath)
                .orElse(FileSystems.getDefault().getPath(StringUtils.EMPTY));
    }

    /**
     * Crea los directorios especificados si no existen.
     *
     * @param path Ruta del directorio que se desea crear.
     * @return `true` si los directorios fueron creados exitosamente, de lo contrario `false`.
     * @throws IOException Si ocurre un error al intentar crear los directorios.
     */
    public static boolean createWorkIfNotExists(Path path) throws IOException {
        if(!Files.exists(path)){
            Files.createDirectories(path);
            return true;
        }
        return false;
    }

    /**
     * Elimina un archivo o directorio si existe.
     *
     * @param path Ruta del archivo o directorio que se desea eliminar.
     * @return `true` si el archivo o directorio fue eliminado exitosamente, de lo contrario `false`.
     * @throws IOException Si ocurre un error al intentar eliminar el archivo o directorio.
     */
    public static boolean deleteWorkIfNotExists(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }
}