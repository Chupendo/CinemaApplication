package com.tokioschool.filmapp.services.user;

import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.records.SearchUserRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de usuarios.
 *
 * Esta interfaz define los métodos necesarios para realizar operaciones relacionadas
 * con la gestión de usuarios, como búsqueda, registro, actualización y autenticación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
public interface UserService {

    /**
     * Busca un usuario por su correo electrónico y devuelve un par con el usuario y su contraseña encriptada.
     *
     * @param mail Correo electrónico del usuario.
     * @return Un {@link Optional} que contiene un par con el {@link UserDto} y la contraseña encriptada.
     */
    Optional<Pair<UserDto, String>> findUserAndPasswordByEmail(String mail);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario.
     */
    Optional<UserDto> findByEmail(String email);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userFormDTO Datos del usuario a registrar.
     * @return Un objeto {@link UserDto} que representa al usuario registrado.
     * @throws IllegalArgumentException Si los datos proporcionados no son válidos.
     */
    UserDto registerUser(UserFormDto userFormDTO) throws IllegalArgumentException;

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param userId ID del usuario a actualizar.
     * @param userFormDTO Datos actualizados del usuario.
     * @return Un objeto {@link UserDto} que representa al usuario actualizado.
     */
    UserDto updateUser(String userId, UserFormDto userFormDTO);

    /**
     * Actualiza la última hora de inicio de sesión del usuario autenticado.
     */
    void updateLastLoginTime();

    /**
     * Busca un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario.
     * @throws AccessDeniedException Si el usuario autenticado no tiene permisos para acceder a la información.
     */
    Optional<UserDto> findById(String userId) throws AccessDeniedException;

    /**
     * Obtiene el usuario autenticado.
     *
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario autenticado.
     */
    Optional<UserDto> findUserAuthenticated();

    /**
     * Busca usuarios según criterios de búsqueda.
     *
     * @param page Número de página.
     * @param pageSize Tamaño de página.
     * @param searchUserRecord Criterios de búsqueda.
     * @return Un objeto {@link PageDTO} que contiene la lista de usuarios encontrados.
     */
    PageDTO<UserDto> searchUsers(int page, int pageSize, SearchUserRecord searchUserRecord);


    boolean operationEditAllow(String userId);
}