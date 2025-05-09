package com.tokioschool.store.authentications.impl;

import com.tokioschool.store.properties.StorePropertiesFilm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StoreAuthenticationServiceImplUTest {

    @Mock
    public StorePropertiesFilm storePropertiesFilm;

    @Mock
    public RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    public StoreAuthenticationServiceImpl storeAuthenticationService;

    private static final String RESOURCE_PATH = "/store/api/auth";
    private static final String USERNAME_LOGIN_DEFAULT  =   "user";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storeAuthenticationService, "USERNAME_LOGIN_DEFAULT", "user");
    }

    @Test
    void givenNewAuthenticationByDefault_whenGetAccessToken_thenTokenIsGenerated() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000  ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);

        String token = storeAuthenticationService.getAccessToken();
        assertNotNull(token);
    }

    @Test
    void givenValidUserName_whenGetAccessToken_thenTokenIsReturned() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000 ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);

        String token = storeAuthenticationService.getAccessToken("test");
        assertNotNull(token);
    }

    @Test
    void givenNullUserName_whenGetAccessToken_thenTokenIsGenerated() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000 ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);

        String token = storeAuthenticationService.getAccessToken(null);

        assertEquals("secret", token);
    }

    @Test
    void givenWhitespaceUserName_whenGetAccessToken_thenTokenIsGenerated() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000 ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);

        String token = storeAuthenticationService.getAccessToken("   ");

        assertEquals("secret", token);
    }

    @Test
    void givenEmptyUserName_whenGetAccessToken_thenTokenIsGenerated() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000 ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);

        String token = storeAuthenticationService.getAccessToken("");

        assertEquals("secret", token);
    }

    @Test
    void givenExceptionDuringAuthentication_whenGetAccessToken_thenNullIsReturned() {
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenThrow(new RuntimeException("Authentication failed"));

        String token = storeAuthenticationService.getAccessToken("test");

        assertNull(token);
    }

    @Test
    void givenValidToken_whenGetAccessToken_thenSameTokenIsReturned() {
        // set attributes privates of the class, to simulate a valid secret
        ReflectionTestUtils.setField(storeAuthenticationService, "accessToken", "validToken");
        ReflectionTestUtils.setField(storeAuthenticationService, "expiresIn", System.currentTimeMillis() + 3600 * 1000 ); // subtract 10 units, because this performs assures the secret is valid

        String token = storeAuthenticationService.getAccessToken("validToken");

        assertEquals("validToken", token);
    }

    @Test
    void givenExpiredToken_whenGetAccessToken_thenNewTokenIsGenerated() {
        // set attributes privates of the class, to simulate a valid secret
        ReflectionTestUtils.setField(storeAuthenticationService, "accessToken", "secret expired");
        ReflectionTestUtils.setField(storeAuthenticationService, "expiresIn", System.currentTimeMillis() - 1000);

        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);

        StoreAuthenticationServiceImpl.AuthResponseDTO authenticationResponseDTO = StoreAuthenticationServiceImpl.AuthResponseDTO.builder().accessToken("secret").expiresIn( System.currentTimeMillis() + 3600 * 1000 ).build();
        Mockito.when(responseSpec.body(StoreAuthenticationServiceImpl.AuthResponseDTO.class)).thenReturn(authenticationResponseDTO);


        String token = storeAuthenticationService.getAccessToken("test");

        assertEquals("secret", token);
    }

    /**
     * Recollection of the configuration of the mock objects for the test of the method getAccessToken
     * that shared more of at the one unitary case
     *
     */
    private void configurationMockGetTokenW() {
        // mock properties
        StorePropertiesFilm.UserStore userStoreTest = new StorePropertiesFilm.UserStore("test", "passwordEncoded");
        StorePropertiesFilm.UserStore userStoreDefault = new StorePropertiesFilm.UserStore(USERNAME_LOGIN_DEFAULT, "passwordEncoded");
        Mockito.when(storePropertiesFilm.login()).thenReturn(new StorePropertiesFilm.Login(List.of(userStoreTest,userStoreDefault)));
        Mockito.when(storePropertiesFilm.baseUrl()).thenReturn(RESOURCE_PATH);

        //mock rest client
        Mockito.when(restClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.any(String.class), Mockito.any(Object[].class))).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.contentType(Mockito.any(MediaType.class))).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.body(Mockito.any(Map.class))).thenReturn(requestBodyUriSpec);
    }

}