package com.ssafy.staticsserver.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ssafy.staticsserver.infrastructure.dto.KakaoImageResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoClient {
	private final WebClient.Builder webClientBuilder;

	@Value("${KAKAO_API}")
	private String kakaoApiKey;

	public String searchImageUrl(String query) {
		WebClient webClient = webClientBuilder
			.baseUrl("https://dapi.kakao.com")
			.defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoApiKey)
			.build();

		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v2/search/image")
				.queryParam("query", query)
				.queryParam("sort", "accuracy")
				.queryParam("size", 1)
				.build())
			.retrieve()
			.bodyToMono(KakaoImageResponse.class)
			.map(response -> {
				if (response.getDocuments().isEmpty()) {
					return null;
				}
				return response.getDocuments().get(0).getImageUrl();
			})
			.block();
	}
}
