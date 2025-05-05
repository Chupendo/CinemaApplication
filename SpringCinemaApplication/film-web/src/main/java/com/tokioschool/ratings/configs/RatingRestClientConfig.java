package com.tokioschool.ratings.configs;

import com.tokioschool.ratings.authentications.RatingAuth2;
import com.tokioschool.store.authentications.StoreAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RatingRestClientConfig {

    private final RatingProperty ratingProperty;
    private final RatingAuth2 ratingAuth2;

    @Bean("ratingRestClientCredentials")
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl( ratingProperty.baseUrl() )
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE )
                .requestInitializer( request ->
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION,"Bearer %s".formatted( ratingAuth2.getTokenAccess() ) )
                ).build();
    }
}
