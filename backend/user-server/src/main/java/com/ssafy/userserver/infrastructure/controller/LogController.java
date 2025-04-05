package com.ssafy.userserver.infrastructure.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.userserver.application.log.LogService;
import com.ssafy.userserver.common.dto.CommonResponse;
import com.ssafy.userserver.domain.dto.LogRequest;
import com.ssafy.userserver.domain.dto.NewsListResponse;
import com.ssafy.userserver.domain.dto.NewsResponse;

@RestController
@RequestMapping("/api/user/log")
public class LogController {

	private final LogService logService;

	public LogController(LogService logService) {
		this.logService = logService;
	}

	@PostMapping("/access")
	public ResponseEntity<CommonResponse<?>> saveLog(@RequestBody LogRequest logRequest) {
		logService.saveLog(logRequest.getNewsId());
		return ResponseEntity.ok(CommonResponse.success(null));
	}

	@PostMapping("/news")
	public ResponseEntity<CommonResponse<NewsListResponse>> getRecentLogs() {
		List<NewsResponse> newsList = logService.getRecentLogs();
		NewsListResponse responseData = NewsListResponse.builder()
			.news(newsList)
			.build();
		return ResponseEntity.ok(CommonResponse.success(responseData));
	}
}
