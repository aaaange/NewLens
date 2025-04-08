package com.ssafy.userserver.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception)
		throws IOException, ServletException {
		log.error("OAuth2 Authentication Failure: {}", exception.getMessage());

		// 에러 메시지를 URL 인코딩한 후, 실패 페이지로 리다이렉트
		String encodedErrorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
		String redirectUrl = "https://www.newlens.co.kr/login?error=" + encodedErrorMessage;

		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}
