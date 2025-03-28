package com.tokioschool.ratingapp.dtos;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class AuthenticationResponseDTO {

    String accessToken;
    long expiresIn;
}
