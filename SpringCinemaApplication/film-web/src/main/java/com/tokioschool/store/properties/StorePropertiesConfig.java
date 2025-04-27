package com.tokioschool.store.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorePropertiesFilm.class)
public class StorePropertiesConfig {
}
