package com.tokioschool.ratingapp.repositories;

import com.tokioschool.ratingapp.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,String> {

    @Query(value = "select u from User u where UPPER(u.username) = UPPER(?1)  or UPPER(u.email) = UPPER(?1)")
    Optional<User> searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String emailOrUserName);

    Optional<User> findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username,String email);

    List<User> findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}