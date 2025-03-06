package com.tokioschool.ratingapp.securities.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for JWT.
 * This class holds the properties related to JWT configuration, such as the secret key and expiration duration.
 */
@ConfigurationProperties(prefix = "application.jwt")
public record JwtConfigurationProperty(String secret, Duration expiration) {
}