package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Role}.
 *
 * Esta interfaz extiende {@link CrudRepository} para proporcionar métodos CRUD básicos
 * y consultas personalizadas para la entidad Role.
 *
 * Anotaciones:
 * - {@link Repository}: Marca esta interfaz como un componente de acceso a datos de Spring.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Repository
public interface RoleDao extends CrudRepository<Role, Long> {

    List<Role> findAll();
    /**
     * Busca un rol por su nombre, ignorando mayúsculas y minúsculas.
     *
     * @param name El nombre del rol a buscar.
     * @return Una instancia de {@link Role} que coincide con el nombre especificado.
     */
    Role findByNameIgnoreCase(String name);

}