package com.tokioschool.ratingapp.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "yaml")
public class YamlPropertyConfig {
}
