package com.tokioschool.filmapp.services.user.impl;

import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.enums.RoleEnum;
import com.tokioschool.filmapp.records.SearchUserRecord;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.repositories.UserDao;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.filmapp.specifications.UserSpecification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Implementación del servicio de usuarios.
 *
 * Esta clase proporciona métodos para gestionar usuarios, incluyendo
 * registro, actualización, búsqueda y autenticación.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * Busca un usuario por su correo electrónico y devuelve un par con el usuario y su contraseña encriptada.
     *
     * @param mail Correo electrónico del usuario.
     * @return Un {@link Optional} que contiene un par con el {@link UserDto} y la contraseña encriptada.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Pair<UserDto, String>> findUserAndPasswordByEmail(String mail) {
        final String maybeEmail = Optional.ofNullable(mail)
                .map(StringUtils::stripToNull)
                .orElseThrow(() -> new IllegalArgumentException("Email not allow"));

        return userDao.findByUsernameOrEmailIgnoreCase(maybeEmail)
                .map(user -> Pair.of(modelMapper.map(user, UserDto.class), user.getPassword()));
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario.
     * @throws AccessDeniedException Si el usuario autenticado no tiene permisos para acceder a la información.
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        final String maybeEmail = Optional.ofNullable(email)
                .map(StringUtils::stripToNull)
                .orElseThrow(() -> new IllegalArgumentException("Email not allow"));

        final Optional<UserDto> maybeUserDTO = userDao.findByEmailIgnoreCase(maybeEmail)
                .map(user -> modelMapper.map(user, UserDto.class));

        final User userAuth = whoAuthenticated().orElseThrow(() -> new AccessDeniedException("Is required is login"));

        if (isUserAdmin(userAuth) && maybeUserDTO.isPresent() && !maybeUserDTO.get().getId().equals(userAuth.getId())) {
            throw new AccessDeniedException("You don't authorized to information of user");
        }

        return maybeUserDTO;
    }

    @Override
    @Transactional
    public UserDto registerOrUpdatedUser(UserFormDto userFormDTO) {
        User user = Optional.of(userFormDTO)
                .map(UserFormDto::getId)
                .map(userDao::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElseGet(() ->User.builder().build());
        return populationCreateOrEditUser(user, userFormDTO);
    }
    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param userFormDTO Datos del usuario a registrar.
     * @return Un objeto {@link UserDto} que representa al usuario registrado.
     */
    @Override
    @Transactional
    public UserDto registerUser(UserFormDto userFormDTO) {
        User user = User.builder().build();
        return populationCreateOrEditUser(user, userFormDTO);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param userId ID del usuario a actualizar.
     * @param userFormDTO Datos actualizados del usuario.
     * @return Un objeto {@link UserDto} que representa al usuario actualizado.
     * @throws AuthenticationException Si el usuario no está autenticado o no tiene permisos.
     */
    @Override
    @Transactional
    @PreAuthorize(value = "isAuthenticated()")
    public UserDto updateUser(String userId, UserFormDto userFormDTO) {
        whoAuthenticated()
                .filter(user -> Objects.equals(userId, user.getId()) ||
                        (!Objects.equals(userId, user.getId()) && isUserAdmin(user.getRoles())))
                .orElseThrow(() -> new AuthenticationException("User no authenticated") {
                });

        User user = userDao.findById(userId).orElseThrow(() -> new IllegalArgumentException("User don't found"));

        return populationCreateOrEditUser(user, userFormDTO);
    }

    /**
     * Actualiza la última hora de inicio de sesión del usuario autenticado.
     */
    @Override
    @Transactional
    public void updateLastLoginTime() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional.ofNullable(authentication)
                .map(auth -> (UserDetails) auth.getPrincipal())
                .map(UserDetails::getUsername)
                .map(userDao::findByUsernameOrEmailIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .ifPresent(user -> {
                    user.setLastLoginAt(LocalDateTime.now());
                    userDao.save(user);
                });
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario.
     * @throws AccessDeniedException Si el usuario autenticado no tiene permisos para acceder a la información.
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(String userId) throws AccessDeniedException {
        final Optional<UserDto> maybeUserDTO = userId != null ? userDao.findById(userId).map(user -> modelMapper.map(user, UserDto.class)) : Optional.empty();

        final User userAuth = whoAuthenticated().orElseThrow(() -> new AccessDeniedException("Is required is login"));

        if (isUserAdmin(userAuth) && maybeUserDTO.isPresent() && !maybeUserDTO.get().getId().equals(userAuth.getId())) {
            throw new AccessDeniedException("You don't authorized to information of user");
        }

        return maybeUserDTO;
    }

    /**
     * Obtiene el usuario autenticado.
     *
     * @return Un {@link Optional} que contiene el {@link UserDto} del usuario autenticado.
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Optional<UserDto> findUserAuthenticated() {
        return whoAuthenticated().map(user -> modelMapper.map(user, UserDto.class));
    }

    /**
     * Busca usuarios según criterios de búsqueda.
     *
     * @param pageNumber Número de página.
     * @param pageSize Tamaño de página.
     * @param searchUserRecord Criterios de búsqueda.
     * @return Un objeto {@link PageDTO} que contiene la lista de usuarios encontrados.
     */
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public PageDTO<UserDto> searchUsers(int pageNumber, int pageSize, SearchUserRecord searchUserRecord) {
        Specification<User> spec = Specification.allOf();

        if (searchUserRecord != null) {
            spec = Specification
                    .where(UserSpecification.hasUsername(searchUserRecord.username()))
                    .and(UserSpecification.hasSurname(searchUserRecord.surname()))
                    .and(UserSpecification.hasName(searchUserRecord.name()))
                    .and(UserSpecification.containsEmail(searchUserRecord.email()));
        }

        List<UserDto> usersDto = userDao.findAll(spec)
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();

        int startItem = pageNumber * pageSize;
        final int totalPages = pageSize == NumberUtils.SHORT_ZERO ? NumberUtils.SHORT_ONE : (int) Math.ceil((usersDto.size() / (double) pageSize));

        if (startItem >= usersDto.size()) {
            return PageDTO.<UserDto>builder()
                    .items(List.of())
                    .pageSize(pageSize)
                    .pageNumber(pageNumber)
                    .totalPages(totalPages)
                    .build();
        } else {
            if (pageSize == NumberUtils.SHORT_ZERO) {
                usersDto = (List<UserDto>) getItemsPageDto(usersDto, startItem, usersDto.size());
            } else {
                int end = Math.min(startItem + pageSize, usersDto.size());
                usersDto = (List<UserDto>) getItemsPageDto(usersDto, startItem, end);
            }
        }

        return PageDTO.<UserDto>builder()
                .items(usersDto)
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .totalPages(totalPages)
                .build();
    }

    /**
     * Si el usuario autenticado es admin:
     *  * Puede modificar al target si el target no es admin o si está modificándose a sí mismo.
     *
     * Si no es admin:
     *  * Solo puede modificar su propio perfil.
     *
     *  @param userId
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public boolean operationEditAllow(@NonNull String userId) throws AccessDeniedException, UsernameNotFoundException{
        final User authUser = whoAuthenticated()
                .orElseThrow(() -> new AccessDeniedException("Operation not allowed"));

        final User targetUser = userDao.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id %s not found".formatted(userId)));

        final boolean isAuthAdmin = isUserAdmin(authUser.getRoles());
        final boolean isTargetAdmin = isUserAdmin(targetUser.getRoles());
        final boolean isSameUser = Objects.equals(authUser.getId(), targetUser.getId());

        // estrageia
        if (isAuthAdmin) {
            return !isTargetAdmin || isSameUser;
        }

        return isSameUser;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return Optional.ofNullable(id)
                .flatMap(userDao::findById)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }

    /**
     * Obtiene una sublista de elementos para la paginación.
     *
     * @param items Lista de elementos.
     * @param start Índice inicial.
     * @param end Índice final.
     * @return Sublista de elementos.
     */
    private static List<?> getItemsPageDto(List<?> items, int start, int end) {
        return IntStream.range(start, end)
                .mapToObj(items::get)
                .toList();
    }

    /**
     * Registra o actualiza los datos de un usuario.
     *
     * @param user Usuario a registrar o actualizar.
     * @param userFormDTO Datos del usuario.
     * @return Un objeto {@link UserDto} que representa al usuario registrado o actualizado.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected UserDto populationCreateOrEditUser(User user, UserFormDto userFormDTO) {
        if (userFormDTO == null) {
            throw new IllegalArgumentException("The data of user is null");
        }
        user.setName(userFormDTO.getName());
        user.setSurname(userFormDTO.getSurname());
        user.setEmail(userFormDTO.getEmail());

        if (user.getPassword() == null || userFormDTO.isUpdatePassword()) {
            user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
            user.setPasswordBis(passwordEncoder.encode(userFormDTO.getPasswordBis()));
        }

        user.setUsername(userFormDTO.getUsername());
        user.setBirthDate(userFormDTO.getBirthDate());
        user.setRoles(getRolesByName(userFormDTO.getRoles().toArray(new String[0])));

        if (userFormDTO.getImage() != null) {
            user.setImage(UUID.fromString(userFormDTO.getImage()));
        }
        user = userDao.save(user);

        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Obtiene el usuario autenticado.
     *
     * @return Un {@link Optional} que contiene el usuario autenticado.
     */
    private Optional<User> whoAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.nonNull( authentication) && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            return Optional.of((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                    .map(org.springframework.security.core.userdetails.User::getUsername)
                    .map(userDao::findByUsernameOrEmailIgnoreCase)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        }

        return Optional.ofNullable(authentication)
                .map(auth -> (Jwt) auth.getPrincipal())
                .map(Jwt::getClaims)
                .map(claim -> (String) claim.get("sub"))
                .map(userDao::findByUsernameOrEmailIgnoreCase)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Verifica si un conjunto de roles contiene el rol de administrador.
     *
     * @param roles Conjunto de roles.
     * @return true si contiene el rol de administrador, de lo contrario false.
     */
    private boolean isUserAdmin(Set<Role> roles) {
        return roles.stream().map(Role::getName)
                .map(StringUtils::upperCase)
                .anyMatch(roleName -> roleName.equals(RoleEnum.ADMIN.name()));
    }

    /**
     * Obtiene los roles de un usuario a partir de sus nombres.
     *
     * @param roleNames Nombres de los roles.
     * @return Un conjunto de roles.
     */
    private Set<Role> getRolesByName(String... roleNames) {
        Set<Role> roles = new HashSet<>();

        if (roleNames == null || roleNames.length == 0) {
            Role roleUser = roleDao.findByNameIgnoreCase("user");
            if (roleUser != null) {
                roles.add(roleUser);
            }
        } else {
            for (String roleName : roleNames) {
                Role role = roleDao.findByNameIgnoreCase(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    /**
     * Verifica si un usuario tiene el rol de administrador.
     *
     * @param user Usuario.
     * @return true si el usuario tiene el rol de administrador, de lo contrario false.
     */
    private boolean isUserAdmin(User user) {
        return user.getRoles().stream().noneMatch(role -> role.getName().toUpperCase().contains("ADMIN"));
    }
}