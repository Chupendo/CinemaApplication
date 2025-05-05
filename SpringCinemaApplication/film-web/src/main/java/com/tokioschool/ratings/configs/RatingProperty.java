package com.tokioschool.ratings.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rating")
public record RatingProperty(String baseUrl,String grantType,Client client) {

    public record Client(String user,String password){};
}
