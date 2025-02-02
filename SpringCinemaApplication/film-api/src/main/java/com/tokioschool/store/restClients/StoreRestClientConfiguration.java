package com.tokioschool.store.restClients;

import com.tokioschool.store.authentications.StoreAuthenticationService;
import com.tokioschool.store.properties.StoreProperties;
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
public class StoreRestClientConfiguration {

    private final StoreProperties storeProperties;
    private final StoreAuthenticationService storeAuthenticationService;

    @Value("${application.store.login.users[0].username}")
    private String consumer;

    @Value("${application.store.login.users[1].username}")
    private String producer;


    @Bean
    @Qualifier("restClientConsumer")
    public RestClient restConsumerClient(){
        return RestClient.builder()
                .baseUrl(storeProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInitializer(request -> {
                    log.info("Adding token to request as {}",consumer);
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted( storeAuthenticationService.getAccessToken(consumer) ) );
                })
                .build();
    }

    @Bean
    @Qualifier("restClientProducer")
    public RestClient restProducerClient(){
        return RestClient.builder()
                .baseUrl(storeProperties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInitializer(request -> {
                    log.info("Adding token to request as {}",producer);
                    request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted( storeAuthenticationService.getAccessToken(producer) ) );
                })
                .build();
    }

}