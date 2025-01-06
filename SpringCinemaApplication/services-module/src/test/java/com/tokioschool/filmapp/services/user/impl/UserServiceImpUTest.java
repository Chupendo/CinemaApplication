package com.tokioschool.filmapp.services.user.impl;

import com.github.javafaker.Faker;
import com.tokioschool.filmapp.domain.Authority;
import com.tokioschool.filmapp.domain.Role;
import com.tokioschool.filmapp.domain.User;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.repositories.RoleDao;
import com.tokioschool.filmapp.repositories.UserDao;
import org.apache.commons.lang3.tuple.Pair;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                    .password("123")
                    .passwordBis("123")
                    .build();
        }).toList();
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDTO,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getEmail());

        assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }

    @Test
    void givenEmail_whenFindUserAndPasswordByUsername_thenReturnUserDto() {
        Mockito.when(userDao.findByUsernameOrEmailIgnoreCase(users.getFirst().getUsername()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<Pair<UserDTO,String>> maybePairUserDTOPwd = userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        userService.findUserAndPasswordByEmail(users.getFirst().getUsername());

        assertThat(maybePairUserDTOPwd).isPresent()
                .get()
                .returns(users.getFirst().getPassword(), Pair::getRight)
                .extracting(Pair::getLeft)
                .isNotNull()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }

    @Test
    void givenEmail_whenFindByEmail_thenReturnUserDto() {
        Mockito.when(userDao.findByEmailIgnoreCase(users.getFirst().getEmail()))
                .thenReturn(Optional.of(users.getFirst()));

        Optional<UserDTO> maybeUserDTO = userService.findByEmail(users.getFirst().getEmail());

        Mockito.verify(modelMapper,Mockito.times(1)).map(users.getFirst(), UserDTO.class);

        assertThat(maybeUserDTO).isPresent().get()
                .returns(users.getFirst().getEmail(),UserDTO::getEmail)
                .returns(users.getFirst().getName(),UserDTO::getName)
                .returns(users.getFirst().getBirthDate(),UserDTO::getBirthDate)
                .returns(users.getFirst().getCreated(),UserDTO::getCreated);
    }

    @Test
    void givenUserFormDto_whenRegister_thenReturnUserDto(){
        // Arrange
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();
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
        UserDTO reusltUserDto = userService.registerUser(userFormDTO);

        // Assertions
        Mockito.verify(modelMapper,Mockito.times(1)).map(user,UserDTO.class);

        assertThat(reusltUserDto).isNotNull()
                .returns(userFormDTO.getName(),UserDTO::getName)
                .returns(userFormDTO.getSurname(),UserDTO::getSurname)
                .returns(userFormDTO.getUsername(),UserDTO::getUsername)
                .returns(userFormDTO.getRole(),userDto -> userDto.getRoles().getFirst().getName());
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
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();

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
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("user").build();
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
        UserFormDTO userFormDTO = UserFormDTO.builder()
                .id("0002AB")
                .name("John")
                .surname("Doe")
                .password("Contraseña1@")
                .passwordBis("Contraseña1@")
                .username("johndoe")
                .email("johndoe@example.com")
                .birthDate(LocalDate.now().minusYears(30))
                .role("ADMIN").build();

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
        UserDTO userDTO = userService.updateUser(userFormDTO.getId(), userFormDTO);

        assertThat(userDTO).isNotNull();
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
}