package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/users")
@Tag(name="users", description= "users operations")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final StoreFacade storeFacade;

    @Operation(
            summary = "Post register user in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "register user",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "register user failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized, request don't allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "406",
                            description = "Not Acceptable, the data of user not allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "415",
                            description = "HttpMediaTypeNotSupportedException, Unsupported Media Type",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @SecurityRequirement(name = "auth-openapi")
    @PostMapping(value = {"/register","/register/"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<UserFormDTO> registerUserHandler(@RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                       @Valid @RequestPart(value  = "userFormDto") UserFormDTO userFormDTO, BindingResult bindingResult, // del modelo se coge el objeto que se llame userDto
                                                       @Valid @RequestPart(value  = "description" , required = false) String description
    ) throws ValidacionException, BadRequestException {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
           throw new ValidacionException("Errores de validación", errors);
        }
        Optional<ResourceIdDto> resourceIdDtoOptional = Optional.empty();

        try {
           resourceIdDtoOptional = storeFacade.saveResource(multipartFile,description);
           resourceIdDtoOptional.ifPresent(resourceIdDto -> userFormDTO.setImage(resourceIdDto.resourceId().toString()));
            UserDTO userDTO = userService.registerUser(userFormDTO);
            UserFormDTO userFormDTOUpdated = mapperUserDtoToUserFormDto(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(userFormDTOUpdated);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> storeFacade.deleteResource(resourceIdDto.resourceId()) );
            throw new BadRequestException("User don't register", e);
        }
    }

        @PutMapping(value = {"/update/{id}"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "isAuthenticated()")
    public ResponseEntity<UserFormDTO> updateUserHandler(@PathVariable(value = "id") String userId,
                                                     @RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                     @Valid @RequestPart(value  = "userFormDto") UserFormDTO userFormDTO, BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()){
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errores);
        }

        try {
            // TODO agregar gestion de resoruces
            if(multipartFile!=null && !multipartFile.isEmpty()){
                // management of resource
                Optional<ResourceIdDto> resourceIdDtoOptional;
                if(userFormDTO.getImage()!=null){
                    resourceIdDtoOptional = storeFacade.updateResource(UUID.fromString(userFormDTO.getImage()),multipartFile,userId);
                }else{
                    resourceIdDtoOptional = storeFacade.updateResource(null,multipartFile,userId);
                }
                resourceIdDtoOptional.ifPresent(resourceIdDto -> userFormDTO.setImage(resourceIdDto.resourceId().toString()));
            }

            UserDTO userDTO = userService.updateUser(userId,userFormDTO);
            UserFormDTO userFormDTOUpdated = mapperUserDtoToUserFormDto(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(userFormDTOUpdated);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            throw new BadRequestException("User don't register", e);
        }
    }


    private UserFormDTO mapperUserDtoToUserFormDto(@NonNull UserDTO userDTO){
            return UserFormDTO.builder()
                    .id(userDTO.getId())
                    .name(userDTO.getName())
                    .surname(userDTO.getSurname())
                    .roles(userDTO.getRoles().stream().map(RoleDTO::getName).toList())
                    .birthDate(userDTO.getBirthDate())
                    .email(userDTO.getEmail())
                    .created(userDTO.getCreated())
                    .image(userDTO.getResourceId())
                    .build();
    }
}
