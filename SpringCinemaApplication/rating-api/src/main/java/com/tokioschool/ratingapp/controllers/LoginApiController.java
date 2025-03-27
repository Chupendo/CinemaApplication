package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.auth.resources.OAuth2TokenService;
import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LoginApiController {

    private final OAuth2TokenService oAuth2TokenService;

    public LoginApiController(OAuth2TokenService oAuth2TokenService) {
        this.oAuth2TokenService = oAuth2TokenService;
    }

    @GetMapping("/login/oauth2/code/oidc-client-web")
    public ResponseEntity<OAuth2TokenResponse> authorizationWeb(@RequestParam(value = "code") String code) throws BadRequestException {
        log.debug("authorizaction: "+code);

        return ResponseEntity.ok( oAuth2TokenService.getAccessToken(code) );
    }

    @GetMapping("/login/oauth2/code/oidc-client")
    public ResponseEntity<String> authorization(@RequestParam(value = "code") String code) throws BadRequestException {
        log.debug("authorizaction: "+code);

        return ResponseEntity.ok( code );
    }
}
