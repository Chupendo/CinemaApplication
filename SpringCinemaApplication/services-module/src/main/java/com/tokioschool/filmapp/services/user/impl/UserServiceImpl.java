package com.tokioschool.filmapp.services.user.impl;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.repositories.UserDao;
import com.tokioschool.filmapp.services.user.UserService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true) // aseguras que los datos relacionados con carga perezosa pueden cargarse durante la transacción.
    public Optional<Pair<UserDTO, String>> findUserAndPasswordByEmail(String mail) {
        final String maybeEmail = Optional.ofNullable(mail)
                .map(StringUtils::stripToNull)
                .orElseThrow(()->new IllegalArgumentException("Email not allow"));

        // devuelve una tupa con el usuarioDTO y la pwd encriptada dentro de un optional
        // o un optional vacio
        return userDao.findByUsernameOrEmailIgnoreCase(maybeEmail)
                .map(user ->  Pair.of(modelMapper.map(user, UserDTO.class), user.getPassword()));
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        final String maybeEmail = Optional.ofNullable(email)
                .map(StringUtils::stripToNull)
                .orElseThrow(()->new IllegalArgumentException("Email not allow"));

        // devuelve una tupa con el usuarioDTO o un optional vacio
        return userDao.findByEmailIgnoreCase(maybeEmail)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    @Transactional
    @PreAuthorize(value = "hasRole('ADMIN')")
    public UserDTO registerUser(UserFormDTO userFormDTO) {
        User user = User.builder().build();
        return populationCreateOrEditUser(user,userFormDTO);
    }

    /**
     * Register or Updated the data user in the system
     *
     * @param user if empty is new, otherwise is updated
     * @param userFormDTO data of user
     * @return user to object
     */
    protected UserDTO populationCreateOrEditUser(User user,UserFormDTO userFormDTO){
        if(userFormDTO == null){
            throw new IllegalArgumentException("The data of user is null");
        }
        user.setId(userFormDTO.getId());
        user.setName(userFormDTO.getName());
        user.setSurname(userFormDTO.getSurname());
        user.setEmail(userFormDTO.getEmail());
        user.setPassword(userFormDTO.getPassword());
        user.setPasswordBis(userFormDTO.getPasswordBis());
        user.setUsername(userFormDTO.getUsername());
        user.setBirthDate(userFormDTO.getBirthDate());

        // roles
        Role roleUser = Optional.ofNullable(userFormDTO.getRole())
                .map(roleDao::findByNameIgnoreCase)
                .orElseGet(() ->roleDao.findByNameIgnoreCase("user"));
        user.setRoles(Set.of(roleUser));

        user.setCreated(LocalDateTime.now());
        user.setLastLoginAt(LocalDateTime.now());

        user = userDao.save(user);

        return modelMapper.map(user,UserDTO.class);
    }
}
