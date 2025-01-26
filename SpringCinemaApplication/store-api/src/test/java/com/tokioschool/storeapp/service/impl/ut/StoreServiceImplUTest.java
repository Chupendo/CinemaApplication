package com.tokioschool.storeapp.service.impl.ut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import com.tokioschool.storeapp.service.impl.StoreServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;


@ActiveProfiles("test")
class StoreServiceImplUTest {


    private static StoreService storeService;

    @TempDir
    public static Path temporalPath;

    // CONSTANTS
    public static final String EXT_TXT = ".txt";
    private static final String FILE_NAME = "file%s".formatted(EXT_TXT);
    private static final String CONTENT = "HOLA";
    private static final String CONTENT_TYPE = MediaType.TEXT_PLAIN_VALUE;

    @BeforeAll
    public static void init(){
        StoreConfigurationProperties storeConfigurationProperties = new StoreConfigurationProperties(temporalPath.toAbsolutePath(),temporalPath.toString());
        storeService = new StoreServiceImpl(storeConfigurationProperties,new ObjectMapper());
    }

    @Test
    void givenResource_whenSaveResource_thenReturnOk() {
        MockMultipartFile multipartFile = getMockMultipartFile();

       Optional<ResourceIdDto> optionalResourceIdDto = storeService.saveResource(multipartFile,null);

        Assertions.assertThat(optionalResourceIdDto)
                .isNotNull()
                .isNotEmpty()
                .matches(resourceIdDto -> Objects.nonNull(resourceIdDto.get().resourceId()))
                .matches(resourceIdDto ->
                        Files.exists(Path.of(temporalPath.toString(),resourceIdDto.get().resourceId().toString()))
                ).matches(resourceIdDto ->
                                Files.exists(Path.of(temporalPath.toString(),
                                        "%s.json".formatted(resourceIdDto.get().resourceId().toString()))
                )
                );
    }

    @Test
    void givenResource_whenFindResource_thenReturnOk() {
        final ResourceIdDto resourceIdDto = storeService.saveResource(getMockMultipartFile(), "description").get();

        final Optional<ResourceContentDto> optionalResourceContentDto = storeService
                .findResource(resourceIdDto.resourceId());

        Assertions.assertThat(optionalResourceContentDto)
                .isPresent()
                .isNotEmpty()
                .get()
                .returns(resourceIdDto.resourceId(),ResourceContentDto::resourceId)
                .returns(CONTENT.getBytes(),ResourceContentDto::content)
                .returns(FILE_NAME,ResourceContentDto::resourceName)
                .returns(CONTENT_TYPE,ResourceContentDto::contentType);
    }

    @Test
    void givenResource_whenDeleteResource_thenReturnOk() {
        final ResourceIdDto resourceIdDto = storeService
                .saveResource(getMockMultipartFile(), "description").get();

        storeService.deleteResource(resourceIdDto.resourceId());

        // verify
        final Optional<ResourceContentDto> optionalResourceContentDto = storeService
                .findResource(resourceIdDto.resourceId());

        Assertions.assertThat(optionalResourceContentDto)
                .isEmpty();
    }

    private static MockMultipartFile getMockMultipartFile() {

        return new MockMultipartFile(
                FILE_NAME.replace(EXT_TXT, StringUtils.EMPTY),
                FILE_NAME,
                CONTENT_TYPE,
                CONTENT.getBytes());

    }
}