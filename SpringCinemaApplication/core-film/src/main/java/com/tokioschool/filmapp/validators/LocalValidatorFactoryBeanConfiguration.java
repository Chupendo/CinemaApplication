package com.tokioschool.filmapp.validators;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
//@RequiredArgsConstructor
public class LocalValidatorFactoryBeanConfiguration {

    // ID
    //private final MessageSource messageSource;

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        final LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        // defined the message source as messages validation
        //localValidatorFactoryBean.setValidationMessageSource(messageSource);

        localValidatorFactoryBean.afterPropertiesSet(); // apply the custom configurations
        return localValidatorFactoryBean;
    }
}
