package com.tokioschool.filmapp.restTemplate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    @Qualifier("restClientEmpty")
    public RestClient restClient(){
        return RestClient.builder().build();
    }
}