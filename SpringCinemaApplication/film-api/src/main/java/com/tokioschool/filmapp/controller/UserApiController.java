package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.helpers.UUIDHelper;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        if ( !validationRolNewUser(userFormDTO.getRoles()) ){
            Map<String,String> error = Collections.singletonMap("roles","The rol of new user don't allow");
            throw new ValidacionException("Errores de validación", error);
        }

        Optional<ResourceIdDto> resourceIdDtoOptional = Optional.empty();

        try {
            resourceIdDtoOptional = storeFacade.registerResource(multipartFile,description);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> userFormDTO.setImage(resourceIdDto.resourceId().toString()));
            UserDTO userDTO = userService.registerUser(userFormDTO);
            UserFormDTO userFormDTOUpdated = mapperUserDtoToUserFormDto(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(userFormDTOUpdated);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> storeFacade.deleteResource(resourceIdDto.resourceId()) );
            throw new BadRequestException("User don't register, because %s".formatted(e.getMessage()), e);
        }
    }

    @PutMapping(value = {"/update/{id}"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "isAuthenticated()")
    public ResponseEntity<UserFormDTO> updateUserHandler(@PathVariable(value = "id") String userId,
                                                         @RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                         @RequestPart(value = "description",required = false) String description,
                                                         @Valid @RequestPart(value  = "userFormDto") UserFormDTO userFormDTO, BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()){
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errores);
        }

        if ( !validationRolNewUser(userFormDTO.getRoles()) ){
            Map<String,String> error = Collections.singletonMap("roles","The rol of new user don't allow");
            throw new ValidacionException("Errores de validación", error);
        }

        try {
            if(multipartFile!=null && !multipartFile.isEmpty()){
                // management of resource
                Optional<ResourceIdDto> resourceIdDtoOptional = storeFacade.saveResource(multipartFile,description);
                resourceIdDtoOptional.ifPresent(resourceIdDto -> {
                    UUIDHelper.mapStringToUUID(  userFormDTO.getImage() ).ifPresent(storeFacade::deleteResource);
                    userFormDTO.setImage( resourceIdDto.resourceId().toString() );
                });
            }

            UserDTO userDTO = userService.updateUser(userId,userFormDTO);
            UserFormDTO userFormDTOUpdated = mapperUserDtoToUserFormDto(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(userFormDTOUpdated);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            throw new BadRequestException("User don't register", e);
        }
    }

    /**
     * Validate that user or an anonymous user don't create a new user type admin
     *
     * @param roles collection roles of new user
     * @return true if the type roles is allowed, otherwise false
     */
    private boolean validationRolNewUser(List<String> roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        boolean isAdmin = false;
        for (String authority : authorities){// verify if own quest is type ADMIN
            if (authority.equalsIgnoreCase("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        boolean typeUserAllow = true;
        if( !isAdmin ){ // valid that rol isn't ADMIN
            for(String role : roles ){
                if( role.toUpperCase().contains( "ADMIN" ) ){
                    typeUserAllow = false;
                    break;
                }
            }
        }

        return typeUserAllow;
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
