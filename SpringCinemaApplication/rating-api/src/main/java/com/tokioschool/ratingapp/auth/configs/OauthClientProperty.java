package com.tokioschool.ratingapp.auth.configs;

import com.tokioschool.ratingapp.auth.dtos.Auth2Client;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "oauth2")
public record OauthClientProperty(Auth2Client clientOauth,Auth2Client clientOidc,Auth2Client clientOidcWeb){}
