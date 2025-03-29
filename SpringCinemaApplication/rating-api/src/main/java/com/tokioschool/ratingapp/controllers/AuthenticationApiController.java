package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.dtos.AuthenticatedMeResponseDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationRequestDTO;
import com.tokioschool.ratingapp.dtos.AuthenticationResponseDTO;
import com.tokioschool.ratingapp.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = {"/login"},consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<AuthenticationResponseDTO> loginApiHandler(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) throws Exception {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO) );
    }

    @GetMapping(value={"/me"},produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMeHandler() throws LoginException {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }

}
