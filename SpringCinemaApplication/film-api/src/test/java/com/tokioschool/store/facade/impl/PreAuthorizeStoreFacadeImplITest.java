package com.tokioschool.store.facade.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tokioschool.filmapp.security.filter.FilmApiSecurityConfiguration;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Import({FilmApiSecurityConfiguration.class})
@ActiveProfiles("test")
class PreAuthorizeStoreFacadeImplITest {

    @Autowired
    private StoreFacade storeFacade;


    @MockitoBean
    @Qualifier("restClientConsumer") // si se pone, no ejecuta el getAccessToken()
    private RestClient restClient; // Se usa MockBean en lugar de Mock, para cargar el contexto de Spring completo

    @Test
    void saveResourceWithUserNotAuthenticated_withValidData_returnsAuthenticationCredentialsNotFoundException() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, ()-> storeFacade.saveResource(multipartFile, "test description") );

    }

    @Test
    @WithAnonymousUser
    void saveResourceWithUserAnonymousForFilmApi_withSaveResource_returnsAuthorizationDeniedException() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        Assertions.assertThrows(AuthorizationDeniedException.class, ()-> storeFacade.saveResource(multipartFile, "test description") );

    }

    @Test
    @WithMockUser(username = "username",authorities = "read-resource",roles = {"USER"})
    void saveResourceWith_withSaveResource_returnsResourceId() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());

        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        Mockito.when(restClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri( Mockito.anyString())).thenReturn(requestBodySpec);

        Mockito.when(requestBodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(requestBodySpec);

        Mockito.when(requestBodySpec.body(Mockito.any(Map.class))).thenReturn(requestBodySpec);
        Mockito.when(requestBodySpec.body(Mockito.any(MultiValueMap.class))).thenReturn(requestBodySpec);

        Mockito.when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(ResourceIdDto.class)).thenReturn(resourceIdDto);


        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertTrue(result.isPresent());
        assertEquals(resourceIdDto, result.get());

    }

    @Test
    @WithMockUser(username = "username",authorities = "read-resource",roles = {"USER"})
    void saveResourceWithUser_withValidData_returnsAAccessDeniedExceptionForStoreApi() {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        Mockito.when(restClient.post()).thenThrow(new AccessDeniedException("Access is denied"));

        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertTrue(result.isEmpty());
    }

    @Test
    void findResourceId_withUserNonAuthenticated_returnAuthenticationCredentialsNotFoundException() throws JsonProcessingException {
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());

        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, ()-> storeFacade.findResource(resourceIdDto.resourceId()) );
    }

    @Test
    @WithMockUser(username = "username",authorities = "read-resource",roles = {"USER"})
    void findResourceId_withValidData_returnResourceContentDto() {
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());
        ResourceContentDto resourceContentDto = ResourceContentDto.builder().resourceId(resourceIdDto.resourceId()).content("hola".getBytes()).size("hola".length()).build();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        Mockito.when(restClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.anyString(), Mockito.any(UUID.class))).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(ResourceContentDto.class)).thenReturn(resourceContentDto);

        Optional<ResourceContentDto> result = storeFacade.findResource(resourceIdDto.resourceId());

        assertTrue(result.isPresent());
    }

    @Test
    void deleteResourceId_withUserNonAuthenticated_returnAuthenticationCredentialsNotFoundException() throws JsonProcessingException {
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());

        Assertions.assertThrows(AuthenticationCredentialsNotFoundException.class, ()-> storeFacade.deleteResource(resourceIdDto.resourceId()) );
    }

    @Test
    @WithMockUser(username = "username",authorities = "read-resource",roles = {"USER"})
    void deleteResourceId_withUserAuthenticated_returnOk() {
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());
        ResourceContentDto resourceContentDto = ResourceContentDto.builder().resourceId(resourceIdDto.resourceId()).content("hola".getBytes()).size("hola".length()).build();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        Mockito.when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(Mockito.anyString(), Mockito.any(UUID.class))).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(ResourceContentDto.class)).thenReturn(resourceContentDto);

        storeFacade.deleteResource(resourceIdDto.resourceId());

        Mockito.verify(restClient.delete()).uri(Mockito.anyString(), Mockito.any(UUID.class));
    }


}