package com.tokioschool.ratingapp.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for JWT.
 * This class holds the properties related to JWT configuration, such as the secret key and expiration duration.
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperty(String token, Duration expiration){}
