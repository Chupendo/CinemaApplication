package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,String> {

    Optional<User> findUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username,String email);

    List<User> findUsersByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);
}
