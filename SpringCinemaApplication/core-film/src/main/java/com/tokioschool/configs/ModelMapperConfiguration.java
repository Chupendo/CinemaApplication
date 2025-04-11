package com.tokioschool.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para el bean de \{@link ModelMapper\}.
 *
 * Esta clase define un bean de ModelMapper que se utiliza para realizar
 * conversiones y mapeos entre entidades y DTOs en la aplicación.
 *
 * \{@link ModelMapper\} es una biblioteca que simplifica el mapeo de objetos
 * al proporcionar una forma flexible y eficiente de configurar las reglas de conversión.
 *
 * @author andres
 * @version 1.0
 */
@Configuration
public class ModelMapperConfiguration {

    /**
     * Define un bean de \{@link ModelMapper\} que estará disponible en el contexto de Spring.
     *
     * Este bean se puede inyectar en otras clases para realizar mapeos entre objetos.
     *
     * @return Una instancia de \{@link ModelMapper\}.
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}