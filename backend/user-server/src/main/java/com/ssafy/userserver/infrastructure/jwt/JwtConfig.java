package com.ssafy.userserver.infrastructure.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class JwtConfig {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.access-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-expiration}")
	private long refreshTokenExpiration;

	@Bean
	public String getSecretKey() {
		return secretKey;
	}

	@Bean
	public long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	@Bean
	public long getRefreshTokenExpiration() {return refreshTokenExpiration; }
}
