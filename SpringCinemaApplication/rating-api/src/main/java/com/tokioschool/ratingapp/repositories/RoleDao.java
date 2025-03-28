package com.tokioschool.ratingapp.repositories;

import com.tokioschool.ratingapp.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends JpaRepository<Role,Long> {
}
