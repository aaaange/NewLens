// package com.ssafy.springgateway.filter;
//
// import org.springframework.cloud.gateway.filter.GatewayFilter;
// import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;
//
// @Component
// public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
//
// 	public JwtAuthenticationFilter() {
// 		super(Config.class);
// 	}
//
// 	public static class Config {
// 		// 추가 설정 (예: 검증 키, 토큰 만료 시간 등)
// 	}
//
// 	@Override
// 	public GatewayFilter apply(Config config) {
// 		return (exchange, chain) -> {
// 			ServerHttpRequest request = exchange.getRequest();
// 			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
// 				// 인증 헤더 없으면 401 응답
// 				exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
// 				return exchange.getResponse().setComplete();
// 			}
// 			// 실제 JWT 검증 로직을 수행 (토큰 파싱, 유효성 체크 등)
// 			// 성공 시, 다음 체인으로 요청 전달
// 			return chain.filter(exchange);
// 		};
// 	}
// }
