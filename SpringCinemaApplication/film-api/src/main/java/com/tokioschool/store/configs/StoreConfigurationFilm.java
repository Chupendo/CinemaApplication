package com.tokioschool.store.configs;

import com.tokioschool.store.properties.StorePropertiesFilm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorePropertiesFilm.class)
public class StoreConfigurationFilm {
}
