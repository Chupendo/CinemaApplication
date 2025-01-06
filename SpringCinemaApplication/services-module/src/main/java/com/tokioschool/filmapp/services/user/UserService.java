package com.tokioschool.filmapp.services.user;


import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public interface UserService {

    Optional<Pair<UserDTO,String>> findUserAndPasswordByEmail(String mail);
    Optional<UserDTO> findByEmail(String email);
    Optional<UserDTO> registerUser(UserFormDTO userFormDTO);
}
