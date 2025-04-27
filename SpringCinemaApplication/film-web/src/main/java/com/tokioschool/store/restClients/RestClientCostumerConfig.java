package com.tokioschool.store.restClients;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.properties.StorePropertiesFilm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestClientCostumerConfig {

    @Value("${application.store.base-url:http://localhost:9091}")
    private String baseUrl;

    private final StoreAuthenticationService storeAuthenticationService;

    @Bean("restClientCostumer")
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl( baseUrl )
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE )
                .requestInitializer( request ->
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION,"Bearer %s".formatted( storeAuthenticationService.getAccessToken()) )
                ).build();
    }
}
