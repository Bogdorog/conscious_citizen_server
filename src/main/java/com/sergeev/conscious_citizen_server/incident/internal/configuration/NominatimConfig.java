package com.sergeev.conscious_citizen_server.incident.internal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NominatimConfig {

    @Bean
    public WebClient nominatimWebClient() {

        return WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(HttpHeaders.USER_AGENT, "conscious-citizen-server")
                .build();
    }

}