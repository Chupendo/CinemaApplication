package com.tokioschool.storeapp.userdetails.configuration;


import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StoreUserConfigurationProperty.class)
public class StoreUserConfiguration {
}
