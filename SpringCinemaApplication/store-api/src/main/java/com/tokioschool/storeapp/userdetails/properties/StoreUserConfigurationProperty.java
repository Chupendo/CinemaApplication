package com.tokioschool.storeapp.userdetails.properties;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.store.login")
public record StoreUserConfigurationProperty(List<UserDto> users){

}
