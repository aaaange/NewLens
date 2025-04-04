package com.ssafy.springgateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("user-service", r -> r
				.path("/api/user/**")
				// .filters(f -> f.stripPrefix(1))
				// JwtAuthenticationFilter를 필터 체인에 적용
				// .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config())))
				.uri("http://user-server:9001"))
			.route("search-service", r -> r
				.path("/api/search/**")
				// .filters(f -> f.stripPrefix(1))
				.uri("http://search-server:9002"))
			.build();
	}
}
