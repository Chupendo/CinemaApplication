package com.tokioschool.store.configs;

import com.tokioschool.store.properties.StoreProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StoreProperties.class)
public class StoreConfiguration {
}
