package com.tokioschool.ratingapp.services;

import com.tokioschool.ratingapp.dto.users.UserDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public interface UserService {
    Optional<Pair<UserDto,String>> findUserAndPasswordByEmail(String emailOrUsername);
    Optional<UserDto> findByEmail(String email);
}
