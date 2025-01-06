package com.tokioschool.filmapp.services.user.impl;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.repositories.UserDao;
import com.tokioschool.filmapp.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

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
    //@PreAuthorize(value = "hasRole('ADMIN')")
    public UserDTO registerUser(UserFormDTO userFormDTO) {
        User user = User.builder().build();
        return populationCreateOrEditUser(user,userFormDTO);
    }

    @Override
    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public UserDTO updateUser(String userId, UserFormDTO userFormDTO) {
        // verify the operation
        whoAuthenticated()
                .filter(user -> Objects.equals(userId,user.getId()) ||
                        (!Objects.equals(userId,user.getId()) && isUserAdmin(user.getRoles()) ) )
                .orElseThrow(() -> new AuthenticationException("User no authenticated") {
                });

        User user = userDao.findById(userId).orElseThrow(()-> new IllegalArgumentException("User don't found"));

        return populationCreateOrEditUser(user,userFormDTO);
    }

    /**
     * Register or Updated the data user in the system
     *
     * @param user if empty is new, otherwise is updated
     * @param userFormDTO data of user
     * @return user to object
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected UserDTO populationCreateOrEditUser(User user,UserFormDTO userFormDTO){
        if(userFormDTO == null){
            throw new IllegalArgumentException("The data of user is null");
        }
        //user.setId(userFormDTO.getId());
        user.setName(userFormDTO.getName());
        user.setSurname(userFormDTO.getSurname());
        user.setEmail(userFormDTO.getEmail());

        if(user.getPassword() == null || userFormDTO.isUpdatePassword() ) {
            user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
            user.setPasswordBis(passwordEncoder.encode(userFormDTO.getPasswordBis()));
        }

        user.setUsername(userFormDTO.getUsername());
        user.setBirthDate(userFormDTO.getBirthDate());

        // roles
        user.setRoles(getRolesByName(userFormDTO.getRole()));

        user = userDao.save(user);

        return modelMapper.map(user,UserDTO.class);
    }

    private Optional<User> whoAuthenticated(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .map(auth ->  (Jwt) auth.getPrincipal())
                .map(Jwt::getClaims)
                .map(claim-> (String) claim.get("sub"))
                .map(userDao::findByUsernameOrEmailIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Given a collection of roles, check if contains the role Admin
     *
     * @param roles collection
     * @return true if contains the rol Admin, otherwise false
     */
    private boolean isUserAdmin(Set<Role> roles){
        return roles.stream().map(Role::getName)
                .map(StringUtils::upperCase)
                .anyMatch(roleName -> roleName.equals(RoleEnum.ADMIN.name()));
    }

    /**
     * Obtained the roles of user given name role, if this isn't name, then
     * return role user by default
     *
     * @param roleNames
     * @return
     */
    private Set<Role> getRolesByName(String... roleNames){
        Set<Role> roles = new HashSet<>();

        // Verifica si roleNames es nulo o está vacío
        if (roleNames == null || roleNames.length == 0) {
            // Si no se proporcionan nombres de roles, asigna el rol "user" por defecto
            Role roleUser = roleDao.findByNameIgnoreCase("user");
            if (roleUser != null) {
                roles.add(roleUser);
            }
        } else {
            // Si se proporcionan nombres de roles, busca cada uno
            for (String roleName : roleNames) {
                Role role = roleDao.findByNameIgnoreCase(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }
}
