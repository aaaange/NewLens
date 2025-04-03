package com.ssafy.searchserver.infrastructure.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
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

	@Value("${elasticsearch.username}")
	private String esUsername;

	@Value("${elasticsearch.password}")
	private String esPassword;

	@Bean
	public RestClient restClient() {
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esUsername, esPassword));

		return RestClient.builder(
				new HttpHost(esHost, esPort, esScheme))
			.setHttpClientConfigCallback(httpClientBuilder ->
				httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
			.build();
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
