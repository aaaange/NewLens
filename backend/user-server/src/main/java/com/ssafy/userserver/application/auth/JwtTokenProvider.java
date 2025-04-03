// package com.ssafy.userserver.application.auth;
//
// import com.ssafy.userserver.domain.entity.User;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import org.springframework.stereotype.Component;
// import java.util.Date;
// import java.util.Set;
//
// @Component
// public class JwtTokenProvider {
//
// 	// JWT 서명용 비밀키 (실제 서비스에서는 안전하게 보관)
// 	private final String SECRET_KEY = "YOUR_JWT_SECRET_KEY";
//
// 	// Access Token (예: 30분 유효)
// 	public String createAccessToken(User user, Set<String> roles) {
// 		long now = System.currentTimeMillis();
// 		long expiry = 1000L * 60 * 30; // 30분
// 		return Jwts.builder()
// 			.setSubject(user.getEmail())
// 			.claim("userId", user.getId())
// 			.claim("roles", roles)
// 			.setIssuedAt(new Date(now))
// 			.setExpiration(new Date(now + expiry))
// 			.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
// 			.compact();
// 	}
//
// 	// Refresh Token (예: 2주 유효)
// 	public String createRefreshToken(User user, Set<String> roles) {
// 		long now = System.currentTimeMillis();
// 		long expiry = 1000L * 60 * 60 * 24 * 14; // 2주
// 		return Jwts.builder()
// 			.setSubject(user.getEmail())
// 			.claim("userId", user.getId())
// 			.claim("roles", roles)
// 			.setIssuedAt(new Date(now))
// 			.setExpiration(new Date(now + expiry))
// 			.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
// 			.compact();
// 	}
// }
