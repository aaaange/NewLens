package com.ssafy.staticsserver.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey;

    public String translateText(String text) {
        if (text == null || text.isBlank()) return text;

        WebClient webClient = webClientBuilder
                .baseUrl("https://translation.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();

        Mono<String> responseMono = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/language/translate/v2")
                        .queryParam("key", googleApiKey)
                        .build())
                .bodyValue(Map.of(
                        "q", text,
                        "target", "ko",
                        "format", "text",
                        "model", "nmt"
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> {
                    var data = (Map<?, ?>) body.get("data");
                    var translations = (java.util.List<?>) data.get("translations");
                    var translatedText = (Map<?, ?>) translations.get(0);
                    return translatedText.get("translatedText").toString();
                });

        return responseMono.block();
    }
}