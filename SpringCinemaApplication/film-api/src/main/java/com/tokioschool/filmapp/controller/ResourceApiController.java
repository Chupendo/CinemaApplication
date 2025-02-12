package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.store.dto.ResourceContentDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/resources")
@Tag(name="Resource", description= "Resources operations, integration with store-api")
@Slf4j
public class ResourceApiController {

    private final StoreFacade storeFacade;

    @Operation(
            summary = "Get search by filter title or/and range release year",
            description = "Search for movies by providing a title and/or a range of release years. You can also define pagination parameters. If \"pageSize\" is 0, then return all movies filters in a page.",
            parameters = {
                    @Parameter(
                            name = "resourceId",
                            description = "Identification of resource (default: 4888138f-4a56-42a7-9bd6-da2751ded046)",
                            required = false,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of content of resoruce",
                            content = @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request (e.g., malformed filter)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Resource not found or Server Remote is disconnected)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"/",""},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getContentResourceHandler(@RequestParam("resourceId") @Nonnull UUID resourceId) {
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id: %s not found! or Store Server is disconnected.".formatted(resourceId)));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resourceContentDto.contentType()))
                .contentLength(resourceContentDto.size())
                .body(resourceContentDto.content());
    }

    @Operation(
            summary = "Get search by filter title or/and range release year",
            description = "Search for movies by providing a title and/or a range of release years. You can also define pagination parameters. If \"pageSize\" is 0, then return all movies filters in a page.",
            parameters = {
                    @Parameter(
                            name = "resourceId",
                            description = "Identification of resource (default: 4888138f-4a56-42a7-9bd6-da2751ded046)",
                            required = false,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful download of content of resource",
                            content = @Content(
                                    mediaType = "*/*",
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request (e.g., malformed filter)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Resource not found or Server Remote is disconnected)",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @GetMapping(value = {"/download"},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadContentResourceHandler(@RequestParam("resourceId") @Nonnull UUID resourceId) {
        final ResourceContentDto resourceContentDto = storeFacade.findResource(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id: %s not found! or Store Server is disconnected.".formatted(resourceId)));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resourceContentDto.resourceName());
        headers.add(HttpHeaders.CONTENT_LENGTH,String.valueOf(resourceContentDto.size()));
        headers.add(HttpHeaders.CONTENT_TYPE,resourceContentDto.contentType());

        return  ResponseEntity.ok()
                .headers(headers)
                .body(resourceContentDto.content());
    }
}
