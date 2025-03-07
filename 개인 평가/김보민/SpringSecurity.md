## ❄️ 스프링 시큐리티 설치

```java
// build.gradle

dependencies {
    (... 생략 ...)
    implementation 'org.springframework.boot:spring-boot-starter-security'
    // implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity6'
}
```

## 🫧 스프링 시큐리티 설정

![image.png](attachment:32494e21-ea92-4168-a7ad-8521e565353c:image.png)

- 시큐리티 설치 후 인증을 위한 로그인 화면 뜸 ⇒ 해결해야 함

```java
// SecurityConfig.java

package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
				.csrf((csrf) -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
				.headers((headers) -> headers.addHeaderWriter(
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));
		return http.build();
	}
}
```

`@Configuration` : 이 파일이 스프링의 환경 설정 파일임을 의미하는 애너테이션

`@EnableWebSecurity` 

- 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션
- 내부적으로 SecurityFilterChain 클래스가 동작하여 모든 요청 URL에 이 클래스가 필터로 적용되어 URL별로 특별한 설정 가능

```java
http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
```

- 인증되지 않은 모든 페이지의 요청을 허락한다는 의미

### H2 콘솔 오류 수정하기

![image.png](attachment:5d4910d1-cb5d-4ceb-aeb4-90e68086b17d:image.png)

- 403 Forbidden : 작동 중인 서버에 요청이 들어왔으나, 서버가 클라이언트 접근을 거부할 때 발생
- 원인 : 스프링 시큐리티의 CSRF 방어 기능에 의해 H2 콘솔 접근이 거부
- 스프링 시큐리티
    - CSRF 토큰을 세션을 통해 발행
    - 웹 페이지에서는 폼 전송 시에 해당 토큰을 함께 전송
    - 실제 웹 페이지에서 작성한 데이터가 전달되는지를 검증

```java
.csrf((csrf) -> csrf
.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
```

- `/h2-console/`로 시작하는 모든 URL은 CSRF 검증을 하지 않는다는 코드

### 화면이 깨져보이는 문제

![image.png](attachment:c8d6d334-0c92-4839-8e90-8fbd78d341ea:image.png)

- 원인 : 스프링 시큐리티는 웹 사이트의 콘텐츠가 다른 사이트에 포함되지 않도록 하기 위해 X-Frame-Options 헤더의 기본값을 DENY로 사용하기 때문

```java
.headers((headers) -> headers.addHeaderWriter(
						new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));
		return http.build();
```

- `X-Frame-Options` 헤더를 DENY 대신 SAMEORIGIN으로 설정
- 프레임에 포함된 웹 페이지가 동일한 사이트에서 제공할 때에만 사용 허락