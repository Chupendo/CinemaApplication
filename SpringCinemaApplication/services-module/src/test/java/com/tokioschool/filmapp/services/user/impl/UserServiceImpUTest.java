package com.tokioschool.filmapp.services.user.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
import com.tokioschool.filmapp.records.SearchUserRecord;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.repositories.UserDao;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImpUTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper  modelMapper;

    private static final Faker faker = new Faker();
    private static  List<User> users;


    @BeforeAll
    public static void beforeAll(){
        users = IntStream.range(0,10).mapToObj(i->{
            String name = faker.name().name();
            Random random = new Random();
            return User.builder()
                    .email("%s@.%s.com".formatted(name,faker.company().suffix()))
                    .name(name)
                    .surname(faker.name().lastName())
                    .username(faker.dragonBall().character())
                    .birthDate(LocalDate.now().minusYears(random.nextLong(10,40)))
                    .created(LocalDateTime.now())
                    .lastLoginAt(LocalDateTime.now())
                    .roles(Set.of(Role.builder().name("ROLE_ADMIN").build()))
                    .password("123")
                    .passwordBis("123")
                    .build();
        }).toList();
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDto,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDto.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(), UserDto::getEmail)
                .returns(users.getFirst().getName(), UserDto::getName)
                .returns(users.getFirst().getBirthDate(), UserDto::getBirthDate)
                .returns(users.getFirst().getCreated(), UserDto::getCreated);
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByUsername_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getUsername()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDto,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDto.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(), UserDto::getEmail)
                .returns(users.getFirst().getName(), UserDto::getName)
                .returns(users.getFirst().getBirthDate(), UserDto::getBirthDate)
                .returns(users.getFirst().getCreated(), UserDto::getCreated);
    }

    @Test
    void givenEmail_whenFindByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        // Simulate a JWT with a secret value and expiration date
        Instant expirationTime = Instant.now().plusSeconds(3600);
        Jwt jwt = Jwt.withTokenValue("mocked-jwt-secret")
                .header("alg", "HS256")
                .claim("sub", users.getFirst().getEmail())
                .claim("authorities", users.getFirst().getRoles())
                .expiresAt(expirationTime)
                .build();

        // Create the authentication secret with the mocked JWT
        JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(jwt, Collections.emptyList());

        // Mock the security context to set the authenticated user
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);
        SecurityContextHolder.setContext(securityContext);

        Optional<UserDto> maybeUserDTO = userService.findByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper, Mockito.times(1)).map(users.getFirst(), UserDto.class);

        assertThat(maybeUserDTO).isPresent().get()
                .returns(users.getFirst().getEmail(), UserDto::getEmail)
                .returns(users.getFirst().getName(), UserDto::getName)
                .returns(users.getFirst().getBirthDate(), UserDto::getBirthDate)
                .returns(users.getFirst().getCreated(), UserDto::getCreated);

        SecurityContextHolder.clearContext();
    }

    @Test
    void givenUserFormDto_whenRegister_thenReturnUserDto(){
        // Arrange
        UserFormDto userFormDTO = UserFormDto.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of("user")).build();
        Role role = Role.builder().id(1L).name("user")
                .authorities(Set.of(
                                Authority.builder().id(1L).name("read").build()
                        )
                ).build();
        User user = User.builder()
                .id("0001AB")
                .name("John")
                .surname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(Set.of(role))
                .build();

        Mockito.when(roleDao.findByNameIgnoreCase("user")).thenReturn(role);
        Mockito.when(passwordEncoder.encode(userFormDTO.getPassword())).thenReturn("encryptedPassword");
        Mockito.when(userDao.save(Mockito.any(User.class))).thenReturn(user);


        // Act
        UserDto reusltUserDto = userService.registerUser(userFormDTO);

        // Assertions
        Mockito.verify(modelMapper,Mockito.times(1)).map(user, UserDto.class);

        assertThat(reusltUserDto).isNotNull()
                .returns(userFormDTO.getName(), UserDto::getName)
                .returns(userFormDTO.getSurname(), UserDto::getSurname)
                .returns(userFormDTO.getUsername(), UserDto::getUsername)
                .returns(userFormDTO.getRoles().getFirst(), userDto -> userDto.getRoles().getFirst().getName());
    }

    @Test
    void givenTokenNull_whenUpdated_thenReturnUserNoAuthenticated(){
        // Crear una autenticación simulada
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(null, "USER");

        // Crear un contexto de seguridad vacío
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // Establecer el contexto de seguridad en el SecurityContextHolder
        SecurityContextHolder.setContext(context);

        // Arrange
        UserFormDto userFormDTO = UserFormDto.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of("user")).build();

        // Act
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                userService.updateUser(userFormDTO.getId(), userFormDTO));

        assertThat(exception).returns("User no authenticated",AuthenticationException::getMessage);
    }

    @Test
    void givenUserAuthenticated_whenUpdatedOtherUser_thenReturnNoAuthorized(){
        // Crear una autenticación simulada
        Jwt jwt = Jwt.withTokenValue("asdfasdf").claim("sub","johndoe").header("a","a").build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt, "USER");

        // Crear un contexto de seguridad vacío
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // Establecer el contexto de seguridad en el SecurityContextHolder
        SecurityContextHolder.setContext(context);

        // Arrange
        UserFormDto userFormDTO = UserFormDto.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of("user")).build();
        Role role = Role.builder().id(1L).name("USER")
                .authorities(Set.of(
                                Authority.builder().id(1L).name("read").build()
                        )
                ).build();
        // users with distinct id
        User userAuth = User.builder()
                .id("0001AB")
                .name("John")
                .surname("Doe")
                .password("encrypt@")
                .passwordBis("encrypt@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(Set.of(role))
                .build();

        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase("johndoe")).thenReturn(Optional.of(userAuth));

        // Act
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                userService.updateUser(userFormDTO.getId(), userFormDTO));

        assertThat(exception).returns("User no authenticated",AuthenticationException::getMessage);
    }

    @Test
    void givenAdminAuthenticated_whenUpdatedOtherUser_thenReturnOk(){
        // Crear una autenticación simulada
        Jwt jwt = Jwt.withTokenValue("asdfasdf").claim("sub","ADMIN").header("a","a").build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt, "ADMIN");

        // Crear un contexto de seguridad vacío
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // Establecer el contexto de seguridad en el SecurityContextHolder
        SecurityContextHolder.setContext(context);

        // Arrange
        UserFormDto userFormDTO = UserFormDto.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(List.of("ADMIN")).build();

        // users with distinct id
        Role role = Role.builder().id(1L).name("ADMIN")
                .authorities(Set.of(
                                Authority.builder().id(2L).name("write").build()
                        )
                ).build();
        User user = User.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("encrypt@")
                .passwordBis("encrypt@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(Set.of(role))
                .build();

        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase("ADMIN")).thenReturn(Optional.of(user));
        Mockito.when(userDao.findById(userFormDTO.getId())).thenReturn(Optional.of(user));
        Mockito.when(userDao.save(user)).thenReturn(user);

        // Act
        UserDto userDTO = userService.updateUser(userFormDTO.getId(), userFormDTO);

        assertThat(userDTO).isNotNull();
    }

    @Test
    void givenUserAuthenticated_whenFindUserAuthenticated_thenReturnUser() {
        // Crear una autenticación simulada
        Jwt jwt = Jwt.withTokenValue("asdfasdf").claim("sub", "ADMIN").header("a", "a").build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(jwt, "ADMIN");

        // Crear un contexto de seguridad vacío
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        // Establecer el contexto de seguridad en el SecurityContextHolder
        SecurityContextHolder.setContext(context);

        // users with distinct id
        Role role = Role.builder().id(1L).name("ADMIN")
                .authorities(Set.of(
                                Authority.builder().id(2L).name("write").build()
                        )
                ).build();

        User user = User.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("encrypt@")
                .passwordBis("encrypt@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .roles(Set.of(role))
                .build();

        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase("ADMIN")).thenReturn(Optional.of(user));

        Optional<UserDto> userDTOOptional = userService.findUserAuthenticated();

        Assertions.assertThat(userDTOOptional)
                .isPresent()
                .get()
                .satisfies(userDTO -> userDTO.getBirthDate().equals(user.getBirthDate()));
    }

    @Test
    void givenNotUserAuthenticated_whenFindUserAuthenticated_thenReturnUserEmpty() {
        Optional<UserDto> userDTOOptional = userService.findUserAuthenticated();
        Assertions.assertThat(userDTOOptional)
                .isEmpty();
    }

    @Test
    void givenSearchCriteriaProvided_whenSearchUsers_thenReturnsPagedUsers() {
        SearchUserRecord searchUserRecord = new SearchUserRecord("username", "surname", "name", "email@example.com");
        User user = new User();
        when(userDao.findAll(any(Specification.class))).thenReturn(List.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(new UserDto());

        PageDTO<UserDto> result = userService.searchUsers(0, 10, searchUserRecord);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void givenNoUsersFound_whenSearchUsers_thenReturnsEmptyPage() {
        SearchUserRecord searchUserRecord = new SearchUserRecord("username", "surname", "name", "email@example.com");
        when(userDao.findAll(any(Specification.class))).thenReturn(List.of());

        PageDTO<UserDto> result = userService.searchUsers(0, 10, searchUserRecord);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }
    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}