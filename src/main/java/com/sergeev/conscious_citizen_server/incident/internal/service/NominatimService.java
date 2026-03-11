package com.sergeev.conscious_citizen_server.incident.internal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class NominatimService {

    private final WebClient webClient;

    public NominatimService(WebClient nominatimWebClient) {
        this.webClient = nominatimWebClient;
    }

    public String reverse(double lat, double lon) {

        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("format", "json")
                        .queryParam("accept-language", "ru")
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("display_name");
    }
}
