package com.tokioschool.ratingapp.controllers;

import com.tokioschool.ratingapp.auth.resources.OAuth2TokenService;
import com.tokioschool.ratingapp.core.responses.OAuth2TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class OAuth2AuthorizationController {

    private final OAuth2TokenService oAuth2TokenService;

    @GetMapping("/authenticate")
    public ResponseEntity<OAuth2TokenResponse> authenticate(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam("grant_type") String grantType) throws BadRequestException {
        log.debug("authorizaction: "+authorization+", grant_type:"+grantType);
        if( !"client_credentials".equals( grantType ) || !authorization.startsWith("Basic ") ){
            throw new BadRequestException("error in grant type or authorization");
        }

        return ResponseEntity.ok( oAuth2TokenService.exchangeAuthorizationCodeForAccessToken(authorization.replace("Basic ","")) );
    }

}