package com.tokioschool.store.facade.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.store.dto.ResourceIdDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class StoreFacadeImplUTest {
    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private StoreFacadeImpl storeFacade;

    // MOCK for Rest Client
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private static final String RESOURCE_URL = "/store/api/resource";

    @Test
    void saveResource_withValidData_returnsResourceId() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        Map<String, String> descriptionMap = new HashMap<>();
        descriptionMap.put("description", "test description");
        Mockito.when(objectMapper.writeValueAsString(descriptionMap)).thenReturn("{\"description\":\"test description\"}");

        // mock request more response
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());
        Mockito.when(responseSpec.body(ResourceIdDto.class)).thenReturn(resourceIdDto);

        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertTrue(result.isPresent());
        assertEquals(resourceIdDto, result.get());
    }

    @Test
    void saveResource_withInvalidContentType_returnsResourceId() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", null, "test".getBytes());

        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"description\":\"test description\"}");

        // mock request more response
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        ResourceIdDto resourceIdDto = new ResourceIdDto(UUID.randomUUID());
        Mockito.when(responseSpec.body(ResourceIdDto.class)).thenReturn(resourceIdDto);

        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertTrue(result.isPresent());
        assertEquals(resourceIdDto, result.get());
    }

    @Test
    void saveResource_withFailRestClient_returnsOptionalEmpty() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"description\":\"test description\"}");


        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertFalse(result.isPresent());
    }

    @Test
    void saveResource_withJsonProcessingException_returnsEmpty() throws JsonProcessingException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Error") {});

        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertFalse(result.isPresent());
    }

    @Test
    void saveResource_withExceptionDuringSave_returnsEmpty() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"description\":\"test description\"}");

        MultipartFile multipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());

        // mock request more response
        configurationMockGetTokenW();
        Mockito.when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.body(ResourceIdDto.class)).thenThrow(new RuntimeException("Error saving resource"));

        Optional<ResourceIdDto> result = storeFacade.saveResource(multipartFile, "test description");

        assertFalse(result.isPresent());
    }

    /**
     * Recollection of the configuration of the mock objects for the test of the method getAccessToken
     * that shared more of at the one unitary case
     *
     */
    private void configurationMockGetTokenW() {
        //mock rest client
        Mockito.when(restClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(Mockito.any(String.class), Mockito.any(Object[].class))).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.contentType(Mockito.any(MediaType.class))).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.body(Mockito.any(Map.class))).thenReturn(requestBodyUriSpec);
    }
}