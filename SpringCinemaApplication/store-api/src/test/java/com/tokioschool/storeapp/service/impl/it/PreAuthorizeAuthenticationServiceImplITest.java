package com.tokioschool.storeapp.service.impl.it;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class PreAuthorizeAuthenticationServiceImplITest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    public void givenUserLogin_whenGetAuthenticatedWithAdminRole_returnOk() throws Exception {
        AuthenticatedMeResponseDTO response = authenticationService.getAuthenticated();
        Assertions.assertNotNull(response);
        Assertions.assertEquals("adminUser", response.getUsername());
    }

    @Test
    @WithMockUser(username = "regularUser", roles = {"USER"})
    public void givenUserLogin_whenGetAuthenticatedWithUserRole_returnAuthorizationDeniedException() {
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

    @Test
    public void givenUserNotLogin_whenGetAuthenticated_returnAuthenticationCredentialsNotFoundException() {
        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

    @Test
    @WithAnonymousUser
    public void givenUWithAnonymousUser_whenGetAuthenticated_returnAuthorizationDeniedException() {
        Assertions.assertThrows(AuthorizationDeniedException.class, () -> {
            authenticationService.getAuthenticated();
        });
    }

}
