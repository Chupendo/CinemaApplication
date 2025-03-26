package com.tokioschool.ratingapp.auth.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "oauth2.client")
public record OauthClientProperty(String clientId,
                                  String clientSecret,
                                  Set<String> scopes,
                                  String redirectUri,
                                  String authorizationUri,
                                  String tokenUri,
                                  String userInfoUri,
                                  String userNameAttributeName ){}
