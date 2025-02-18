package com.tokioschool.store.facade.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreFacadeImpl implements StoreFacade {

    @Qualifier("restClientConsumer")
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String RESOURCE_URL = "/store/api/resource";

    @Override
    public Optional<ResourceIdDto> registerResource(MultipartFile multipartFile, String description) {

        // simulation of description as request part
        Map<String,String> descriptionMap = new HashMap<>();
        descriptionMap.put("description",description);
        String descriptionBody = StringUtils.EMPTY;
        try{
            descriptionBody = objectMapper.writeValueAsString(descriptionMap);
        }catch (JsonProcessingException e){
            log.error("Error while parsing file", e);
        }

        // simulation of content file as request part
        MediaType mediaType;
        try{
            mediaType = MediaType.valueOf(multipartFile.getContentType());

        }catch (Exception e){
            log.error("Error while parsing file", e);
            mediaType = MediaType.APPLICATION_OCTET_STREAM; // it save as binary strict
        }

        // each part is a different object, and will is send as pair header more body
        final HttpEntity<Object> descriptionPart = buildHttpEntity(MediaType.APPLICATION_JSON,descriptionBody);
        final HttpEntity<Object> resourcePart = buildHttpEntity(mediaType,multipartFile.getResource());

        // mount the request body with multipart request parts
        MultiValueMap<Object,Object> parts = new LinkedMultiValueMap<>();
        parts.add("description",descriptionPart);
        parts.add("content",resourcePart);

        try{
            final ResourceIdDto resourceIdDto = restClient.post()
                    .uri(RESOURCE_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(ResourceIdDto.class);

            return Optional.ofNullable(resourceIdDto);
        } catch (Exception e) {
            log.error("Error saving resource",e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, String description) {

        // simulation of description as request part
        Map<String,String> descriptionMap = new HashMap<>();
        descriptionMap.put("description",description);
        String descriptionBody = StringUtils.EMPTY;
        try{
            descriptionBody = objectMapper.writeValueAsString(descriptionMap);
        }catch (JsonProcessingException e){
            log.error("Error while parsing file", e);
        }

        // simulation of content file as request part
        MediaType mediaType;
        try{
            mediaType = MediaType.valueOf(multipartFile.getContentType());

        }catch (Exception e){
            log.error("Error while parsing file", e);
            mediaType = MediaType.APPLICATION_OCTET_STREAM; // it save as binary strict
        }

        // each part is a different object, and will is send as pair header more body
        final HttpEntity<Object> descriptionPart = buildHttpEntity(MediaType.APPLICATION_JSON,descriptionBody);
        final HttpEntity<Object> resourcePart = buildHttpEntity(mediaType,multipartFile.getResource());

        // mount the request body with multipart request parts
        MultiValueMap<Object,Object> parts = new LinkedMultiValueMap<>();
        parts.add("description",descriptionPart);
        parts.add("content",resourcePart);

        try{
            final ResourceIdDto resourceIdDto = restClient.post()
                    .uri(RESOURCE_URL)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(parts)
                    .retrieve()
                    .body(ResourceIdDto.class);

            return Optional.ofNullable(resourceIdDto);
        } catch (Exception e) {
            log.error("Error saving resource",e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ResourceContentDto> findResource(UUID resourceId) {
        final String uri = "%s/{resourceId}".formatted(RESOURCE_URL);
        try{
            ResourceContentDto resourceContentDto = restClient.get()
                    .uri(uri,resourceId)
                    .retrieve()
                    .body(ResourceContentDto.class);

            return Optional.ofNullable(resourceContentDto);
        }catch (Exception e){
            log.error("Exception in findResource",e);
        }

        return Optional.empty();
    }

    @Override
    public void deleteResource(UUID resourceId) {
        final String uri = "%s/{resourceId}".formatted(RESOURCE_URL);

        restClient.delete().uri(uri,resourceId)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Optional<ResourceIdDto> updateResource(UUID resourceIdOld, MultipartFile multipartFile, String description) {
        Optional<ResourceIdDto> resourceIdDtoOptional = saveResource(multipartFile,description);

        if(resourceIdDtoOptional.isEmpty())
            return Optional.empty();

        if(resourceIdOld!=null){
            deleteResource(resourceIdOld);
        }

        return resourceIdDtoOptional;
    }


    private HttpEntity<Object> buildHttpEntity(MediaType mediaType,Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return new HttpEntity<>(body, headers);
    }
}
