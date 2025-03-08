package com.tokioschool.ratingapp.services.impl;

import com.tokioschool.ratingapp.domains.User;
import com.tokioschool.ratingapp.dto.users.UserDto;
import com.tokioschool.ratingapp.reports.RoleDao;
import com.tokioschool.ratingapp.reports.UserDao;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImplUTest {

    /** dependencies **/
    @Mock private  UserDao userDao;
    @Mock private RoleDao roleDao;
    @Spy private PasswordEncoder passwordEncoder;
    @Spy private ModelMapper modelMapper;

    /** target test **/
    @InjectMocks private UserServiceImpl userService;

    @Test
    void findUserAndPasswordByEmail_returnsUserAndPassword_whenEmailMatches() {
        String email = "testuser@test.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");

        when(userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(email)).thenReturn(Optional.of(user));

        Optional<Pair<UserDto, String>> result = userService.findUserAndPasswordByEmail(email);

        verify(modelMapper,times(1)).map(user, UserDto.class);

        assertThat(result).isPresent();
        assertThat(result.get().getLeft().getEmail()).isEqualTo(email);
        assertThat(result.get().getRight()).isEqualTo("password");
    }

    @Test
    void findUserAndPasswordByEmail_returnsUserAndPassword_whenUsernameMatches() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        when(userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(username)).thenReturn(Optional.of(user));

        Optional<Pair<UserDto, String>> result = userService.findUserAndPasswordByEmail(username);

        verify(modelMapper,times(1)).map(user, UserDto.class);

        assertThat(result).isPresent();
        assertThat(result.get().getLeft().getUsername()).isEqualTo(username);
        assertThat(result.get().getRight()).isEqualTo("password");
    }

    @Test
    void findUserAndPasswordByEmail_throwsException_whenEmailOrUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.findUserAndPasswordByEmail(null));
    }

    @Test
    void findUserAndPasswordByEmail_returnsEmpty_whenNoMatch() {
        String emailOrUsername = "nonexistent";
        when(userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(emailOrUsername)).thenReturn(Optional.empty());

        Optional<Pair<UserDto, String>> result = userService.findUserAndPasswordByEmail(emailOrUsername);

        assertThat(result).isNotPresent();
    }

    @Test
    void findByEmail_returnsUserDto_whenEmailMatches() {
        String email = "testuser@test.com";
        User user = new User();
        user.setEmail(email);

        when(userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(email)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userService.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    void findByEmail_returnsEmpty_whenNoMatch() {
        String email = "nonexistent@test.com";
        when(userDao.searchUserByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(email)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.findByEmail(email);

        assertThat(result).isNotPresent();
    }

    @Test
    void findByEmail_returnEmpty_whenEmailIsNull() {
        assertThat( userService.findByEmail(null)).isEmpty();
    }
}