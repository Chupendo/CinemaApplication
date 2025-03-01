package com.tokioschool.filmapp.repositories;

import com.tokioschool.filmapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,String>, JpaSpecificationExecutor<User> {
    // query methods
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);

    // jpa queries
    @Query(value = "SELECT u FROM User u WHERE UPPER(u.username) = UPPER(:filter) or UPPER(u.email) = UPPER(:filter)")
    Optional<User> findByUsernameOrEmailIgnoreCase(@Param("filter") String filter);
}

