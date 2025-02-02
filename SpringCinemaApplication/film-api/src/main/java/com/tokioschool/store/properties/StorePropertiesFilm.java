package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.store")
public record StorePropertiesFilm(String baseUrl, Login login) {

    public record UserStore(String username, String password) {}
    public record Login( List<UserStore> users ) {}

}
