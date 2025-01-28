package com.tokioschool.storeapp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import com.tokioschool.storeapp.core.helper.FileHelper;
import com.tokioschool.storeapp.domain.ResourceDescription;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final StoreConfigurationProperties storeConfigurationProperties;
    private final ObjectMapper  objectMapper;

    @PostConstruct
    public void init() throws IOException {
        FileHelper.createWorkIfNotExists(Path.of(storeConfigurationProperties.relativePath()));
    }

    /**
     * Upload a resource in the system, as file json and your content with name UUID
     *
     * @param multipartFile resource to upload in the system
     * @param description information additional, this is optional
     * @return the identification of resource encapsulated as ResourceIdDto
     */
    @Override
    public Optional<ResourceIdDto> saveResource(MultipartFile multipartFile, @Nullable String description) {
        // verify resource
        if(multipartFile.isEmpty()){
            log.error("The resource is empty");
            return Optional.empty();
        }

        // build struct data that save in file json
        final ResourceDescription resourceDescription = ResourceDescription.builder()
                .resourceName(multipartFile.getOriginalFilename())
                .description(description)
                .size((int) multipartFile.getSize())
                .contentType(multipartFile.getContentType()).build();

        // generated name of resources
        final ResourceIdDto resourceIdDto = ResourceIdDto.builder().resourceId(UUID.randomUUID()).build();
        final String resourceName = "%s".formatted(resourceIdDto.resourceId());
        final String resourceDescriptionName = "%s.json".formatted(resourceIdDto.resourceId());

        // amount the resource in the path (where upload in the repository), used the relative path
        final Path pathResourceToContent  = storeConfigurationProperties.getResourcePathFromRelativePathGivenNameResource(resourceName);
        final Path pathResourceToDescription = storeConfigurationProperties.getResourcePathFromRelativePathGivenNameResource(resourceDescriptionName);

        // upload the content in the system
        try {
            Files.write(pathResourceToContent,multipartFile.getBytes());

        } catch (IOException e) {
            log.error("Don't save content resource, cause: %s".formatted(e),e);
            return Optional.empty();
        }

        // upolad the met data of resource as JSON Format, encapsulated in object of ResourceDescription
        try {
            objectMapper.writeValue(pathResourceToDescription.toFile(),resourceDescription);
        } catch (IOException e) {
            log.error("Don't save meta data of resource, cause: %s".formatted(e));
            try {
                Files.deleteIfExists(pathResourceToContent);
            } catch (IOException ex) {
                log.error("Error to deleted the resource, cause: %s".formatted(e));
            }
            return Optional.empty();
        }

        // return the id of resource created
        return Optional.of(resourceIdDto);
    }

    /**
     * Find the resource given its id and return it, encapsulated as resource content dto optional
     *
     * @param resourceId identification of resource
     * @return optional with content and description of resource, or optional empty when don't found
     */
    @Override
    public Optional<ResourceContentDto> findResource(UUID resourceId) {
        final String patternFileName  = "%s".formatted(resourceId);
        final String patternFileJsonName  = "%s.json".formatted(resourceId);

        final Optional<File> fileResourceOpt;
        final Optional<File> fileDescriptionResourceOpt;

        // find of resources in the system given its id
        final FilenameFilter filenameFilter = (dir, name) -> name.contains(patternFileName);
        final File[] files = storeConfigurationProperties.buildResourcePathFromRelativePathGivenNameResource()
                .toFile()
                .listFiles(filenameFilter);

        if( files == null || files.length <= 1){
            log.debug("Error to find the resource with id {}, don't found",resourceId);
            return Optional.empty();
        }

        // load files of the resource
        fileResourceOpt = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(patternFileName)).findFirst();
        fileDescriptionResourceOpt = Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(patternFileJsonName)).findFirst();

        if(fileResourceOpt.isEmpty() || fileDescriptionResourceOpt.isEmpty()){
            log.debug("Error the resource with id {}, uncompleted number files",resourceId);
            return Optional.empty();
        }

        // read
        try{
            final byte[] content = Files.readAllBytes(fileResourceOpt.get().toPath());

            final ResourceDescription resourceDescription = this.objectMapper
                    .readValue(fileDescriptionResourceOpt.get(), ResourceDescription.class);

            // build the result end, after read resources
            final ResourceContentDto resourceContentDto = ResourceContentDto.builder()
                    .contentType(resourceDescription.getContentType())
                    .description(resourceDescription.getDescription())
                    .resourceName(resourceDescription.getResourceName())
                    .size(resourceDescription.getSize())
                    .content(content)
                    .resourceId(resourceId)
                    .build();

            return Optional.of(resourceContentDto);

        }catch (IOException e){
            log.error("Error the read the file with id {}, because {}",resourceId,e.getMessage(),e);
            return Optional.empty();
        }
    }

    /**
     * Delete a resource (content more description file) given its id
     *
     * @param resourceId identification of resource
     */
    @Override
    public void deleteResource(UUID resourceId) {
        final FilenameFilter filenameFilter = (dir, name) -> name.contains("%s".formatted(resourceId));
        final File[] filesByName = storeConfigurationProperties
                .buildResourcePathFromRelativePathGivenNameResource()
                .toFile()
                .listFiles(filenameFilter::accept);

        Arrays.stream(filesByName).forEach(file->{
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                log.error("Error in deleteResource, cause: ".formatted(e));
            }
        });
    }
}
