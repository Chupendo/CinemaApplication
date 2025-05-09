package com.tokioschool.storeapp.userdetails.properties;

import com.tokioschool.storeapp.userdetails.dto.UserDto;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Propiedades de configuración para los usuarios de la tienda.
 *
 * Esta clase utiliza un record para definir las propiedades relacionadas con los usuarios
 * que se cargan desde la configuración de la aplicación. Los usuarios se configuran bajo
 * el prefijo `application.store.login` en el archivo de propiedades o YAML.
 *
 * @param users Una lista de usuarios (`UserDto`) definidos en la configuración.
 *
 *  @author andres.rpenuela
 *  @version 1.0
 */
@ConfigurationProperties(prefix = "application.store.login")
public record StoreUserConfigurationProperty(List<UserDto> users) {

}