package com.tokioschool.storeapp.controller;

import com.tokioschool.storeapp.dto.authentication.AuthenticatedMeResponseDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationRequestDTO;
import com.tokioschool.storeapp.dto.authentication.AuthenticationResponseDTO;
import com.tokioschool.storeapp.redis.service.RedisJwtBlackListService;
import com.tokioschool.storeapp.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/api/auth")
@Tag(name="authentication", description= "authentication operations")
public class AuthenticationApiController {

    private final AuthenticationService authenticationService;
    private final RedisJwtBlackListService jwtBlacklistService;

    @Operation(
            summary = "Post authenticated by authentication request dto",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticationResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "authentication failed",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @PostMapping(value = {"","/","/login"},produces = "application/json",consumes = {MediaType.APPLICATION_JSON_VALUE} )
    public ResponseEntity<AuthenticationResponseDTO> postAuthenticated(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequestDTO));
    }

    @Operation(
            summary = "Get data of authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "access denied, Unauthorized",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @GetMapping("/me")
    @SecurityRequirement(name = "auth-openapi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthenticatedMeResponseDTO> getAuthenticatedMe() throws LoginException {
        return ResponseEntity.ok(authenticationService.getAuthenticated());
    }


    @Operation(
            summary = "Get or Post Logout the user of system",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication me response dto",
                            content = @Content(schema = @Schema(implementation = AuthenticatedMeResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "access denied or token invalid",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error internal",
                            content = @Content(schema = @Schema(implementation = Map.class))
                    )
            }
    )
    @RequestMapping(value = "/logout",method = {RequestMethod.GET,RequestMethod.POST})
    @SecurityRequirement(name = "auth-openapi")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutHandler(HttpServletRequest request) throws BadCredentialsException {
        Pair<String,Long> tokenAndExpiredAt = authenticationService.getTokenAndExpiredAt(request);
        if (tokenAndExpiredAt != null && tokenAndExpiredAt.getLeft() != null && tokenAndExpiredAt.getRight() != null) {
            jwtBlacklistService.addToBlacklist(tokenAndExpiredAt.getLeft(),tokenAndExpiredAt.getRight());
            return ResponseEntity.ok().build();
        }
        throw new BadCredentialsException("invalid token is black listed");
    }
}
