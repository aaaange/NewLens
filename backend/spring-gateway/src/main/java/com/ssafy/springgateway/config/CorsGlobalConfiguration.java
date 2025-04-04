// package com.ssafy.springgateway.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.reactive.CorsWebFilter;
// import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//
// import java.util.List;
//
// @Configuration
// public class CorsGlobalConfiguration {
//
// 	@Bean
// 	public CorsWebFilter corsWebFilter() {
// 		CorsConfiguration corsConfig = new CorsConfiguration();
// 		corsConfig.setAllowedOrigins(List.of("*")); // 모든 오리진 허용
// 		corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
// 		corsConfig.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
// 		// corsConfig.setAllowCredentials(true); // 필요 시 설정
//
// 		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
// 		source.registerCorsConfiguration("/**", corsConfig);
//
// 		return new CorsWebFilter(source);
// 	}
// }
