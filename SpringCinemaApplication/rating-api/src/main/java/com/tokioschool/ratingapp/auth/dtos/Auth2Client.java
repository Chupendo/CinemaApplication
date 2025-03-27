package com.tokioschool.ratingapp.auth.dtos;

import java.util.Set;

public record Auth2Client(String clientId,
                                  String clientSecret,
                                  Set<String> scopes,
                                  String redirectUri,
                                  String authorizationUri,
                                  String tokenUri,
                                  String userInfoUri,
                                  String postLogoutRedirectUri,
                                  String userNameAttributeName ){}