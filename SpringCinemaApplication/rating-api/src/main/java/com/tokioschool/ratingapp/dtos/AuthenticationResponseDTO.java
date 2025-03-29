package com.tokioschool.ratingapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class AuthenticationResponseDTO {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("expires_in")
    long expiresIn;
}
