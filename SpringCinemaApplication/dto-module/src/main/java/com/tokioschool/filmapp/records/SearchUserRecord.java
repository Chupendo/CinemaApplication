package com.tokioschool.filmapp.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

/**
 * Registro que representa los criterios de búsqueda de usuarios.
 *
 * Este registro encapsula la información necesaria para realizar una búsqueda
 * de usuarios, incluyendo el nombre, apellido, nombre de usuario y correo electrónico.
 *
 * Es inmutable y se utiliza para transferir datos de manera eficiente.
 *
 * @param name      Nombre del usuario a buscar.
 * @param surname   Apellido del usuario a buscar.
 * @param username  Nombre de usuario a buscar.
 * @param email     Correo electrónico del usuario a buscar.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Builder
@JsonIgnoreProperties
@Jacksonized
public record SearchUserRecord(String name, String surname, String username, String email) {
}