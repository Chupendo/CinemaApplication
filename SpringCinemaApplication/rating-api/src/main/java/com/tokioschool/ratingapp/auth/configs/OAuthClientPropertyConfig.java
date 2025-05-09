package com.tokioschool.ratingapp.auth.configs;

import com.tokioschool.configs.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value="classpath:oauth2-client.yml",encoding = "UTF-8", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(OauthClientProperty.class)
public class OAuthClientPropertyConfig {

}
