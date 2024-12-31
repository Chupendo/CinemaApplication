package com.tokioschool.filmapp.controller;

import com.tokioschool.filmapp.dto.auth.AuthenticatedMeResponseDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationRequestDTO;
import com.tokioschool.filmapp.dto.auth.AuthenticationResponseDTO;
import com.tokioschool.filmapp.services.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/film/api/auth")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<AuthenticationResponseDTO> postAuthenticated(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "auth-openapi")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public Map<String, String> handleBadCredentialsExceptionError(BadCredentialsException ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Map<String, String> handleInternalServerError(Exception ex, HttpServletRequest request) {
        return Map.of("message", ex.getMessage(),"request",request.getRequestURI());
    }
}
