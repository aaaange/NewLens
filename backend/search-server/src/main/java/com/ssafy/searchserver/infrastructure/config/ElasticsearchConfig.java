package com.ssafy.searchserver.infrastructure.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

	@Value("${elasticsearch.host}")
	private String esHost;

	@Value("${elasticsearch.port}")
	private int esPort;

	@Value("${elasticsearch.scheme}")
	private String esScheme;

	@Bean
	public RestClient restClient() {
		return RestClient.builder(
			new HttpHost(esHost, esPort, esScheme)
		).build();
	}

	@Bean
	public RestClientTransport elasticsearchTransport(RestClient restClient) {
		// JacksonJsonpMapper는 JSON 직렬화/역직렬화를 담당합니다.
		return new RestClientTransport(restClient, new JacksonJsonpMapper());
	}

	@Bean
	public ElasticsearchClient elasticsearchClient(RestClientTransport transport) {
		return new ElasticsearchClient(transport);
	}
}
