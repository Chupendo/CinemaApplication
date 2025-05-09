package com.tokioschool.helpers;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Métodos auxiliares para trabajar con UUID.
 *
 * Proporciona métodos estáticos para validar y convertir cadenas en objetos \{@link UUID\}.
 * Incluye una expresión regular para verificar el formato de un UUID.
 *
 * @author  andres.rpeneula
 * @version 1.0
 */
public class UUIDHelper {

    /**
     * Expresión regular para validar el formato de un UUID.
     * Un UUID válido debe seguir el patrón estándar de 8-4-4-4-12 caracteres hexadecimales.
     */
    public static final Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private UUIDHelper() {}

    /**
     * Convierte una cadena en un objeto \{@link UUID\} si la cadena es un UUID válido.
     * Si la cadena no es válida, devuelve un \{@link Optional\} vacío.
     *
     * @param uuid la cadena a convertir en un objeto \{@link UUID\}
     * @return un \{@link Optional\} que contiene el objeto \{@link UUID\} si la cadena es válida,
     *         de lo contrario, un \{@link Optional\} vacío
     */
    public static Optional<UUID> mapStringToUUID(String uuid) {

        // Predicado para verificar si una cadena es un UUID válido
        final Predicate<String> isStrValidUUID = str -> UUID_REGEX.matcher(str).matches();
        return Optional.ofNullable(uuid)
                .map(StringUtils::trimToNull) // Elimina espacios en blanco y convierte cadenas vacías a null
                .filter(isStrValidUUID) // Filtra cadenas que no coincidan con el patrón de UUID
                .map(UUID::fromString); // Convierte la cadena en un objeto UUID
    }

}