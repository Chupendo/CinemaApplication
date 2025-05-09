package com.tokioschool.filmapp.controller;

import com.tokioschool.core.exception.NotFoundException;
import com.tokioschool.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.common.PageDTO;
import com.tokioschool.filmapp.dto.user.RoleDto;
import com.tokioschool.filmapp.dto.user.UserDto;
import com.tokioschool.filmapp.dto.user.UserFormDto;
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

/**
 * Controlador REST para gestionar operaciones relacionadas con usuarios.
 *
 * Este controlador proporciona endpoints para registrar, actualizar, buscar y obtener detalles de usuarios.
 *
 * Anotaciones:
 * - {@link RestController}: Indica que esta clase es un controlador REST.
 * - {@link RequestMapping}: Define la ruta base para los endpoints de este controlador.
 * - {@link Tag}: Proporciona metadatos para la documentación de Swagger.
 *
 * Dependencias:
 * - {@link UserService}: Servicio para manejar la lógica de negocio relacionada con usuarios.
 * - {@link StoreFacade}: Facade para gestionar recursos almacenados, como imágenes.
 * - {@link RegisterUserValidation}: Validador para la validación de datos de usuario.
 *
 * @author andres.rpenuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/users")
@Tag(name = "users", description = "users operations")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final StoreFacade storeFacade;

    /** binding validators **/
    private final RegisterUserValidation registerUserValidation;

    /**
     * Configura el validador para las solicitudes entrantes.
     *
     * @param webDataBinder Binder para asociar el validador.
     */
    @InitBinder
    public void flightMvcDTOValidator(WebDataBinder webDataBinder){
        webDataBinder.setValidator(registerUserValidation);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     *
     * @param multipartFile Archivo de imagen del usuario (opcional).
     * @param userFormDTO Datos del usuario a registrar.
     * @param bindingResult Resultado de la validación de los datos.
     * @param description Descripción del usuario (opcional).
     * @return Respuesta HTTP con código 201 si el registro es exitoso.
     * @throws ValidacionException Si hay errores de validación.
     * @throws BadRequestException Si ocurre un error durante el registro.
     */
    @Operation(
            summary = "Registrar un nuevo usuario",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Errores de validación", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @PostMapping(value = {"/register", "/register/"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> registerUserHandler(@RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                    @Valid @RequestPart(value  = "userFormDto") UserFormDto userFormDTO, BindingResult bindingResult, // del modelo se coge el objeto que se llame userDto
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

    /**
     * Endpoint para actualizar la información de un usuario.
     *
     * @param userId ID del usuario a actualizar.
     * @param multipartFile Archivo de imagen del usuario (opcional).
     * @param description Descripción del usuario (opcional).
     * @param userFormDTO Datos actualizados del usuario.
     * @param bindingResult Resultado de la validación de los datos.
     * @return Respuesta HTTP con los datos del usuario actualizado.
     * @throws BadRequestException Si ocurre un error durante la actualización.
     */
    @Operation(
            summary = "Actualizar información de un usuario",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = UserFormDto.class))),
                    @ApiResponse(responseCode = "400", description = "Errores de validación", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @PutMapping(value = {"/update/{id}"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<UserFormDto> updateUserHandler(@PathVariable(value = "id") String userId,
                                                         @RequestPart(value = "image", required = false) MultipartFile multipartFile, // campo del from
                                                         @RequestPart(value = "description",required = false) String description,
                                                         @Valid @RequestPart(value  = "userFormDto") UserFormDto userFormDTO, BindingResult bindingResult) throws BadRequestException {
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

            UserDto userDTO = userService.updateUser(userId,userFormDTO);
            UserFormDto userFormDTOUpdated = mapperUserDtoToUserFormDto(userDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(userFormDTOUpdated);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            throw new BadRequestException("User don't register", e);
        }
    }

    /**
     * Endpoint para obtener los detalles de un usuario.
     *
     * @param userId ID del usuario (opcional).
     * @return Respuesta HTTP con los detalles del usuario.
     */
    @Operation(
            summary = "Obtener detalles de un usuario",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Detalles del usuario obtenidos exitosamente", content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @GetMapping("/detail/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<UserDto> getUserDetailHandler(@PathVariable(value="id", required = false) String userId){
        final String maybeUserId = Optional.ofNullable(userId).map(StringUtils::trimToNull).orElseGet(()->null);
        UserDto userDTO = null;

        if(Objects.isNull(  maybeUserId ) ){
            userDTO = userService.findUserAuthenticated().orElseThrow(() -> new AccessDeniedException("User not authenticated"));
        }else{
            userDTO = userService.findById(userId).orElseThrow(()->  new NotFoundException("User with id: %s don't found".formatted(userId)));
        }

        return ResponseEntity.ok(userDTO);
    }

    /**
     * Endpoint para buscar usuarios.
     *
     * @param searchUserRecord Criterios de búsqueda.
     * @param page Número de página.
     * @param pageSize Tamaño de la página.
     * @return Respuesta HTTP con los resultados de la búsqueda.
     */
    @Operation(
            summary = "Buscar usuarios",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultados de búsqueda obtenidos exitosamente", content = @Content(schema = @Schema(implementation = PageDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    @PostMapping(value = "/search", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<PageDTO<UserDto>> searchUsersHandler(@RequestPart(required = false,value = "search-user-record") SearchUserRecord searchUserRecord,
                                                               @Min(0) @RequestParam(value = "page", required = false,defaultValue = "0") int page,
                                                               @Min(0) @RequestParam(value = "page-size",required = false,defaultValue = "100") int pageSize){
        PageDTO<UserDto> userPageDTO = userService.searchUsers(page,pageSize,searchUserRecord);
        return ResponseEntity.ok(userPageDTO);
    }

    /**
     * Mapper an instance UserDto to instance UserFormDto
     * @param userDTO information of user to mapper
     * @return information of user mapping as user form dto
     */
    private UserFormDto mapperUserDtoToUserFormDto(@NonNull UserDto userDTO){
        return UserFormDto.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .surname(userDTO.getSurname())
                .roles(userDTO.getRoles().stream().map(RoleDto::getName).toList())
                .birthDate(userDTO.getBirthDate())
                .email(userDTO.getEmail())
                .created(userDTO.getCreated())
                .image(userDTO.getResourceId())
                .build();
    }
}
