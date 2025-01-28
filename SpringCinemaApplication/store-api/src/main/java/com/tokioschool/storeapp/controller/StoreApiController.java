package com.tokioschool.storeapp.controller;

import com.tokioschool.storeapp.core.exception.InternalErrorException;
import com.tokioschool.storeapp.core.exception.NotFoundException;
import com.tokioschool.storeapp.dto.store.ResourceContentDto;
import com.tokioschool.storeapp.dto.store.ResourceIdDto;
import com.tokioschool.storeapp.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/api/resource")
@Tag(name="store", description= "store operations")
public class StoreApiController {

    private final StoreService storeService;

    @Operation(
            summary = "Get resource by resourceId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "resource",
                            content = @Content(schema = @Schema(implementation = ResourceContentDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "authentication failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "file not found",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal ",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping(value = "/{resourceId}",produces = "application/json")
    @SecurityRequirement(name = "auth-openapi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceContentDto> getResourceHandler(
            @NotNull @PathVariable UUID resourceId) {

        ResourceContentDto resourceContentDto = storeService.findResource(resourceId)
                .orElseThrow(()-> new NotFoundException("Resource with id: %s not found!.".formatted(resourceId)));

        return ResponseEntity.ok(resourceContentDto);
    }

    @Operation(
            summary = "Post resource by resourceId",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "resource upload in the system",
                            content = @Content(schema = @Schema(implementation = ResourceIdDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "authentication failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal ",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping(value = {"","/upload"},produces = "application/json",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @SecurityRequirement(name = "auth-openapi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceIdDto> createResourceHandler(
            @RequestPart(value = "description",required = false) String description,
            @RequestPart("content") MultipartFile multipartFile
    ){
        ResourceIdDto resourceIdDto =  storeService.saveResource(multipartFile,description)
                .orElseThrow(() -> new InternalErrorException("There's been an error, try it again later"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resourceIdDto);
    }

    @Operation(
            summary = "Post resource by resourceId",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "resource remove of teh system, no content",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "authentication failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal ",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @DeleteMapping(value="/{resourceId}")
    @SecurityRequirement(name = "auth-openapi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteResourceHandler(@Valid @NotNull @PathVariable UUID resourceId) {
        storeService.deleteResource(resourceId);
        return ResponseEntity.noContent().build();
    }
}
