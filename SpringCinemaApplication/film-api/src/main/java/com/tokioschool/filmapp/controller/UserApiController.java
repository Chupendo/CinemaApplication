package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.core.exception.ValidacionException;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.dto.user.UserDTO;
import com.tokioschool.filmapp.dto.user.UserFormDTO;
import com.tokioschool.filmapp.services.user.UserService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/users")
@Tag(name="users", description= "users operations")
@Slf4j
public class UserApiController {

    private final UserService userService;

    @Operation(
            summary = "Post register user in the system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
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
                            description = "request don't allow",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "406",
                            description = "the data of user not allow",
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
    @PostMapping(value = {"/register"},consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<UserDTO> registerUserHandler(@Valid @RequestBody UserFormDTO userFormDTO, BindingResult bindingResult) throws BadRequestException {
        if(bindingResult.hasErrors()){
            Map<String, String> errores = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
           throw new ValidacionException("Errores de validación", errores);
        }

        try {
            UserDTO userDTO = userService.registerUser(userFormDTO);
            return ResponseEntity.ok(userDTO);
        }catch (Exception e){
            log.error("User don't register because {}",e.getMessage(), e);
            throw new BadRequestException("User don't register", e);
        }
    }
}
