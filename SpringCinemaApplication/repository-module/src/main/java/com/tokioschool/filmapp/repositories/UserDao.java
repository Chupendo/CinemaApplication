package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}.
 *
 * Esta interfaz extiende {@link JpaRepository} y {@link JpaSpecificationExecutor} para proporcionar
 * métodos CRUD, consultas personalizadas y soporte para especificaciones JPA en la entidad User.
 *
 * Anotaciones:
 * - {@link Repository}: Marca esta interfaz como un componente de acceso a datos de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Repository
public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    /**
     * Busca un usuario por su correo electrónico, ignorando mayúsculas y minúsculas.
     *
     * @param email El correo electrónico del usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si existe.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Busca un usuario por su nombre de usuario, ignorando mayúsculas y minúsculas.
     *
     * @param username El nombre de usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si existe.
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Busca un usuario por su nombre de usuario o correo electrónico, ignorando mayúsculas y minúsculas.
     *
     * @param filter El nombre de usuario o correo electrónico a buscar.
     * @return Un {@link Optional} que contiene el usuario si existe.
     */
    @Query(value = "SELECT u FROM User u WHERE UPPER(u.username) = UPPER(:filter) or UPPER(u.email) = UPPER(:filter)")
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("filter") String filter);
}