package com.tokioschool.filmapp.services.user.impl;

import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.repositories.UserDao;
import com.tokioschool.filmapp.services.user.UserService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
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
}
