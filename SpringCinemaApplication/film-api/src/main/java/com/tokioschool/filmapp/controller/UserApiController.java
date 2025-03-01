package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.RoleDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.records.SearchUserRecord;
import com.tokioschool.filmapp.services.user.UserService;
import com.tokioschool.filmapp.validation.RegisterUserValidation;
import com.tokioschool.helpers.UUIDHelper;
import com.tokioschool.store.dto.ResourceIdDto;
import com.tokioschool.store.facade.StoreFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;
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

    /** binding validators **/
    private final RegisterUserValidation registerUserValidation;

    /** is call for each request **/
    @InitBinder
    public void flightMvcDTOValidator(WebDataBinder webDataBinder){
        webDataBinder.setValidator(registerUserValidation);
    }

    @Operation(
            summary = "Post register user in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "register user"
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
    public ResponseEntity<Void> registerUserHandler(@RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
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

        /*
        if ( !validationRolNewUser(userFormDTO.getRoles()) ){
            Map<String,String> error = Collections.singletonMap("roles","The rol of new user don't allow");
            throw new ValidacionException("Errores de validación", error);
        }*/

        Optional<ResourceIdDto> resourceIdDtoOptional = Optional.empty();

        try {
            if(multipartFile!=null && !multipartFile.isEmpty()) {
                resourceIdDtoOptional = storeFacade.registerResource(multipartFile, description);
                resourceIdDtoOptional.ifPresent(resourceIdDto -> userFormDTO.setImage(resourceIdDto.resourceId().toString()));
            }

            userService.registerUser(userFormDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            resourceIdDtoOptional.ifPresent(resourceIdDto -> storeFacade.deleteResource(resourceIdDto.resourceId()) );
            throw new BadRequestException("User don't register, because %s".formatted(e.getMessage()), e);
        }
    }

    @Operation(
            summary = "Update user information",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = UserFormDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation errors",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PutMapping(value = {"/update/{id}"},consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<UserFormDTO> updateUserHandler(@PathVariable(value = "id") String userId,
                                                         @RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                         @RequestPart(value = "description",required = false) String description,
                                                         @Valid @RequestPart(value  = "userFormDto") UserFormDTO userFormDTO, BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()){
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            throw new ValidacionException("Errores de validación", errors);
        }

        /*
        if ( !validationRolNewUser(userFormDTO.getRoles()) ){
            Map<String,String> error = Collections.singletonMap("roles","The rol of new user don't allow");
            throw new ValidacionException("Errores de validación", error);
        }*/

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

    @Operation(
            summary = "Get user details",
            description = "Retrieve the details of a user by their ID. If the ID is not provided, the details of the authenticated user are returned.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful retrieval of user details",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized access (user not authenticated)",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping("/detail/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<UserDTO> getUserDetailHandler(@PathVariable(value="id", required = false) String userId){
        final String maybeUserId = Optional.ofNullable(userId).map(StringUtils::trimToNull).orElseGet(()->null);
        UserDTO userDTO = null;

        if(Objects.isNull(  maybeUserId ) ){
            userDTO = userService.findUserAuthenticated().orElseThrow(() -> new AccessDeniedException("User not authenticated"));
        }else{
            userDTO = userService.findById(userId).orElseThrow(()->  new NotFoundException("User with id: %s don't found".formatted(userId)));
        }

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(value="/search",consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name="auth-openapi")
    public ResponseEntity<PageDTO<UserDTO>> searchUsersHandler(@RequestPart(required = false,value = "search-user-record") SearchUserRecord searchUserRecord,
                                                               @Min(0) @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                               @Min(0) @RequestParam(value = "page-size",required = false,defaultValue = "100") int pageSize){
        PageDTO<UserDTO> userPageDTO = userService.searchUsers(page,pageSize,searchUserRecord);
        return ResponseEntity.ok(userPageDTO);
    }

    /**
     * Mapper an instance UserDto to instance UserFormDto
     * @param userDTO information of user to mapper
     * @return information of user mapping as user form dto
     */
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
