package com.ssafy.userserver.infrastructure.jwt;

import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JWTUtil {

	private SecretKey secretKey;

	public JWTUtil(@Value("${jwt.secret-key}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	public String getEmail(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
	}

	public String getNickname(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
	}

	public Boolean isExpired(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
	}

	public String createJwt(String email, String nickname, Long expiredMs) {

		return Jwts.builder()
			.claim("email", email)
			.claim("nickname", nickname)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}

	// private final JwtConfig jwtConfig;
	//
	// public String generateAccessToken(User user) {
	// 	Date now = new Date();
	// 	Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());
	//
	// 	return Jwts.builder()
	// 		.setSubject(String.valueOf(user.getId()))
	// 		.claim("email", user.getEmail())
	// 		.claim("nickname", user.getNickname())
	// 		.setIssuedAt(now)
	// 		.setExpiration(expiryDate)
	// 		.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey())
	// 		.compact();
	// }
	//
	// public String generateRefreshToken(User user) {
	// 	Date now = new Date();
	// 	Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());
	//
	// 	return Jwts.builder()
	// 		.setSubject(String.valueOf(user.getId()))
	// 		.setIssuedAt(now)
	// 		.setExpiration(expiryDate)
	// 		.signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey())
	// 		.compact();
	// }
	//
	// public boolean validateToken(String token) {
	// 	try {
	// 		Jwts.parser().setSigningKey(jwtConfig.getSecretKey()).parseClaimsJws(token);
	// 		return true;
	// 	} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
	// 		// 로깅 또는 예외 처리
	// 	}
	// 	return false;
	// }
	//
	// public Long getUserIdFromJWT(String token) {
	// 	Claims claims = Jwts.parser()
	// 		.setSigningKey(jwtConfig.getSecretKey())
	// 		.parseClaimsJws(token)
	// 		.getBody();
	//
	// 	return Long.parseLong(claims.getSubject());
	// }
}