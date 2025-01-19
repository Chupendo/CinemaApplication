package com.tokioschool.storeapp.configuration;

import com.tokioschool.storeapp.configuration.properties.StoreConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value= StoreConfigurationProperties.class)
public class StoreConfig {
}
