// package com.ssafy.userserver.infrastructure.security;
//
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.stereotype.Component;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
//
// @Component
// public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {
// 	@Override
// 	public void onAuthenticationFailure(HttpServletRequest request,
// 		HttpServletResponse response,
// 		AuthenticationException exception) throws IOException {
// 		// 로그인 실패 시 로그인 페이지로 에러 메시지 전달 (필요에 따라 수정)
// 		response.sendRedirect("/login?error=" + exception.getMessage());
// 	}
// }
