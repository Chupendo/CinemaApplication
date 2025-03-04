package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.reports.RoleDao;
import com.tokioschool.ratingapp.reports.UserDao;
import com.tokioschool.ratingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of the UserService interface.
 * Provides methods for user-related operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Finds a user by email or username and returns a pair containing the UserDto and the user's password.
     *
     * @param emailOrUsername the email or username of the user
     * @return an Optional containing a Pair of UserDto and password if the user is found, otherwise an empty Optional
     * @throws IllegalArgumentException if the email or username is null or empty
     */
    @Override
    @Transactional(readOnly = true) // this allowed loading the roles, those are lazy
    public Optional<Pair<UserDto, String>> findUserAndPasswordByEmail(String emailOrUsername) throws IllegalArgumentException {
        final String emailOrUsernamesStrip = Optional.ofNullable(emailOrUsername)
                .map(StringUtils::stripToNull)
                .orElseThrow(() -> new IllegalArgumentException("Email or Username not allow"));

        return userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(emailOrUsernamesStrip)
                .map(user -> Pair.of(modelMapper.map(user, UserDto.class), user.getPassword()));
    }

    /**
     * Finds a user by email and returns the UserDto.
     * This method requires the user to be authenticated.
     *
     * @param email the email of the user
     * @return an Optional containing the UserDto if the user is found, otherwise an empty Optional
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public Optional<UserDto> findByEmail(String email) {
        return Optional.ofNullable(email)
                .map(StringUtils::stripToNull)
                .map(userDao::searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase)
                .map(user -> modelMapper.map(user, UserDto.class));
    }
}