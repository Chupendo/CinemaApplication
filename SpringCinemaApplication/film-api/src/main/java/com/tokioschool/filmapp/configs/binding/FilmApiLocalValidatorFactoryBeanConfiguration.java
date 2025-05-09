package com.tokioschool.filmapp.configs.binding;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuración para el bean de \{@link LocalValidatorFactoryBean\} utilizado en la validación de datos.
 *
 * Esta clase define un bean de \{@link LocalValidatorFactoryBean\} que se puede inyectar en otras partes
 * de la aplicación para realizar validaciones personalizadas. Permite configurar un origen de mensajes
 * para las validaciones y aplicar configuraciones personalizadas después de inicializar las propiedades.
 *
 * @version 1.0
 * @author
 */
@Configuration
@RequiredArgsConstructor
public class FilmApiLocalValidatorFactoryBeanConfiguration {

    /**
     * Define un bean de \{@link LocalValidatorFactoryBean\} con configuración personalizada.
     *
     * Este bean permite configurar un origen de mensajes para las validaciones y aplica
     * configuraciones personalizadas después de inicializar las propiedades.
     *
     * @return Una instancia de \{@link LocalValidatorFactoryBean\}.
     */
    @Bean(name = "filmApiLocalValidatorFactoryBean")
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

        // Aplica configuraciones personalizadas después de inicializar las propiedades
        localValidatorFactoryBean.afterPropertiesSet();
        return localValidatorFactoryBean;
    }
}