package com.tokioschool.ratingapp.jwt.properties;

import com.tokioschool.ratingapp.core.config.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value="classpath:json-web-token.yml",encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(JwtProperty.class)
public class JwtPropertyConfig {

}
