package com.tokioschool.ratingapp.securities.jwt.service.impl;

import com.tokioschool.ratingapp.securities.jwt.properties.JwtConfigurationProperty;
import com.tokioschool.ratingapp.securities.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;

/**
 * Service implementation for JWT operations.
 */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtConfigurationProperty jwtConfigurationProperty;
    private final NimbusJwtEncoder nimbusJwtEncoder;

    /**
     * Generates a JWT token for a given user.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     * @throws UnsupportedOperationException if the userDetails is null
     * @throws AccessDeniedException if the user has no roles or privileges
     */
    public Jwt generateToken(UserDetails userDetails) throws UnsupportedOperationException, AccessDeniedException {
        if(userDetails == null){
            throw new UnsupportedOperationException("Operation 'generate token', don't allow.");
        }
        // Header
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        // Claims (payload): a declaración o información sobre un usuario o entidad (estandar + personalizada)
        List<String> authorities =  getAuthoritiesFromUserDetails(userDetails);

        if(authorities.isEmpty()){
            throw new AccessDeniedException("Is require that user has roles and privileges");
        }

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername()) // identifica al usuairo autenticado (sujeto del token)
                .expiresAt(Instant.now().plusMillis(jwtConfigurationProperty.expiration().toMillis())) // fecha y hora exacta en que el token expirará
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) // claim personalizado con los roles y permisos del usuario,
                .build();

        // Signature
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);

        // Encode the token
        return nimbusJwtEncoder.encode(jwtEncoderParameters);
    }

    /**
     * Extracts and converts the authorities from the UserDetails to a list of String objects.
     *
     * @param userDetails the user details
     * @return a list of authorities as String objects
     */
    private List<String> getAuthoritiesFromUserDetails(@NonNull UserDetails userDetails){
        if( userDetails.getAuthorities() == null ){
            return List.of();
        }

        return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    }
}