package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.dtos.AuthenticatedMeResponseDTO;
import com.tokioschool.ratingapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @GetMapping(value={"/me"},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() throws LoginException {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

}
