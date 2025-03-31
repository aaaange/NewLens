package com.ssafy.userserver.infrastructure.jwt;

import com.ssafy.userserver.domain.entity.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final JwtConfig jwtConfig;

	public String generateAccessToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());

		return Jwts.builder()
			.setSubject(String.valueOf(user.getId()))
			.claim("email", user.getEmail())
			.claim("nickname", user.getNickname())
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey())
			.compact();
	}

	public String generateRefreshToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

		return Jwts.builder()
			.setSubject(String.valueOf(user.getId()))
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey())
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
			// 로깅 또는 예외 처리
		}
		return false;
	}

	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(jwtConfig.getSecretKey())
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}
}
