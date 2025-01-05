package com.tokioschool.filmapp.services.jwt.impl;

import com.tokioschool.filmapp.jwt.properties.JwtConfigurationProperty;
import com.tokioschool.filmapp.services.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtConfigurationProperty jwtConfigurationProperty;
    private final NimbusJwtEncoder nimbusJwtEncoder;

    /**
     * Genear tokens para un usuairo
     *
     * @param userDetails
     * @return
     */
    public Jwt generateToken(UserDetails userDetails) {
        // Header
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        // Claims (payload)
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .expiresAt(Instant.now().plusMillis(jwtConfigurationProperty.expiration().toMillis() ))
                .claim("authorities",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        // signarute
        JwtEncoderParameters jwtEncoderParameters =  JwtEncoderParameters.from(jwsHeader, jwtClaimsSet);

        // codifica el token
        return  nimbusJwtEncoder.encode(jwtEncoderParameters);
    }
}
