package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role,Long> {

    // method query
    Role findByNameIgnoreCase(String name);

}
