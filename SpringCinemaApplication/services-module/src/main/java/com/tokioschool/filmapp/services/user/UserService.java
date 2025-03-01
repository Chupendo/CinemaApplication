package com.tokioschool.filmapp.services.user;


import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.records.SearchUserRecord;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<Pair<UserDTO,String>> findUserAndPasswordByEmail(String mail);
    Optional<UserDTO> findByEmail(String email);
    UserDTO registerUser(UserFormDTO userFormDTO) throws IllegalArgumentException;
    UserDTO updateUser(String userId,UserFormDTO userFormDTO);
    void updateLastLoginTime();

    Optional<UserDTO> findById(String userId) throws AccessDeniedException;
    Optional<UserDTO> findUserAuthenticated();

    PageDTO<UserDTO> searchUsers(int page, int pageSize, SearchUserRecord searchUserRecord);
}
