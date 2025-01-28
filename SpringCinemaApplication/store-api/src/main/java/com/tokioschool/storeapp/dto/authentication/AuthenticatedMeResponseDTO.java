package com.tokioschool.storeapp.dto.authentication;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Jacksonized
@Builder
public class AuthenticatedMeResponseDTO {
    String username;
    List<String> authorities;
    List<String> roles;
}
