package com.tokioschool.storeapp.service.impl.ut;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import com.tokioschool.storeapp.security.jwt.service.JwtService;
import com.tokioschool.storeapp.service.impl.AuthenticationServiceImpl;
import com.tokioschool.storeapp.userdetails.dto.UserDto;
import com.tokioschool.storeapp.userdetails.service.StoreUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthenticationServiceImpUTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private StoreUserService storeUserService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void whenUser_whenAuthenticate_theSuccess() throws CredentialException {
        // Arrange
        String username = "testUser";
        String rawPassword = "testPassword";
        String encodedPassword = "encodedPassword";
        UserDto userDto = new UserDto(username, encodedPassword, List.of("read"), List.of("user"));
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("READ"),new SimpleGrantedAuthority("ROLE_USER"));
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username(username)
                .password(rawPassword)
                .build();
        UserDetails userDetails = new User(username,rawPassword,authorities);

        Mockito.when(storeUserService.findByUserName(username)).thenReturn(userDto);

        Authentication authentication = Mockito.mock(Authentication.class);


        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class))).thenReturn(authentication);

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getTokenValue()).thenReturn("jwtToken");
        Mockito.when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class))).thenReturn(jwt);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Act
        AuthenticationResponseDTO response = authenticationService.authenticate(requestDTO);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals("jwtToken", response.getAccessToken());
        org.assertj.core.api.Assertions.assertThat(response.getExpiresIn() ).isGreaterThanOrEqualTo(3600);
        Mockito.verify(securityContext).setAuthentication(authentication);
    }

    @Test
    void whenUser_whenAuthenticate_theUsernameNotFoundException() throws CredentialException {
        // Arrange
        String username = "nonExistentUser";
        String rawPassword = "testPassword";
        AuthenticationRequestDTO requestDTO = AuthenticationRequestDTO.builder()
                .username(username)
                .password(rawPassword)
                .build();

        Mockito.when(storeUserService.findByUserName(username)).thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticate(requestDTO);
        });
    }

    @Test
    void whenUser_whenGetAuthenticated_thenReturnSuccess() throws LoginException {
        // Create a mock Authentication object
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("READ_PRIVILEGE"));
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testUser");
        Mockito.when(authentication.getAuthorities()).thenReturn((List)authorities);

        // Create a mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        // Set the SecurityContextHolder to use the mock SecurityContext
        SecurityContextHolder.setContext(securityContext);

        // Call the method under test
        AuthenticatedMeResponseDTO response = authenticationService.getAuthenticated();

        // Assert the expected results
        Assertions.assertEquals("testUser", response.getUsername());
        Assertions.assertTrue(response.getRoles().contains("ROLE_USER"));
        Assertions.assertTrue(response.getAuthorities().contains("READ_PRIVILEGE"));
    }

    @Test
    void whenUser_whenGetAuthenticated_thenReturnLoginException() throws LoginException {

        // Create a mock SecurityContext
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        // Set the SecurityContextHolder to use the mock SecurityContext
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        Assertions.assertThrows(LoginException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }


    @Test
    void givenRequestWithToken_whenGetTokenAndExpiredAt_thenReturn(){
        final String keyAuth = "Authorization";
        final String starJwtToken = "Bearer ";
        final String tokenValue = "124abc";
        Mockito.when(httpServletRequest.getHeader(keyAuth)).thenReturn("%s%s".formatted(starJwtToken,tokenValue));

        // Create a mock Authentication object as Jwt token
        Jwt  mockJwt = Mockito.mock(Jwt.class);
        Mockito.when(mockJwt.getTokenValue()).thenReturn(tokenValue);
        Mockito.when(mockJwt.getExpiresAt()).thenReturn(Instant.ofEpochSecond(1700000000L));
        //Mockito.when(mockJwt.getClaims()).thenReturn(Map.of("sub", "testUser", "roles", "ADMIN"));

        // Mock authentication and security context
        Authentication authentication = new JwtAuthenticationToken(mockJwt);
        // Create a mock SecurityContext
        SecurityContext mockSecurityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        // Set the SecurityContextHolder to use the mock SecurityContext
        SecurityContextHolder.setContext(mockSecurityContext);

        Pair<String, Long> result =authenticationService.getTokenAndExpiredAt(httpServletRequest);

        Assertions.assertNotNull(result);
    }

    @AfterAll
    static void endTest(){
        // Clearing Security Context
        SecurityContextHolder.clearContext();
    }
}