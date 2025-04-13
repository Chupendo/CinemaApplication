package com.tokioschool.storeapp.userdetails.configuration;

import com.tokioschool.storeapp.userdetails.properties.StoreUserConfigurationProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para las propiedades de usuario de la tienda.
 *
 * Esta clase habilita y carga las propiedades de configuración relacionadas
 * con los usuarios de la tienda, utilizando la clase `StoreUserConfigurationProperty`.
 *
 * @version andres.rpenuela
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(StoreUserConfigurationProperty.class)
public class StoreUserConfiguration {
}