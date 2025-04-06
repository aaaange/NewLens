package com.ssafy.userserver.infrastructure.controller;

import com.ssafy.userserver.application.scrap.ScrapService;
import com.ssafy.userserver.common.dto.CommonResponse;
import com.ssafy.userserver.domain.dto.LogRequest;
import com.ssafy.userserver.domain.dto.ScrapNewsListResponse;
import com.ssafy.userserver.domain.dto.ToggleScrapResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/scrap/news")
public class ScrapController {

	private final ScrapService scrapService;

	public ScrapController(ScrapService scrapService) {
		this.scrapService = scrapService;
	}

	/**
	 * GET /api/scrap/news
	 * 스크랩한 뉴스 목록 조회 (페이징: page, size)
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<ScrapNewsListResponse>> getScrapNews(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size) {
		ScrapNewsListResponse responseData = scrapService.getScrapNews(page, size);
		return ResponseEntity.ok(CommonResponse.success(responseData));
	}

	/**
	 * POST /api/scrap/news/toggle
	 * 스크랩 토글 (추가/삭제)
	 * Body 예제: { "news_id": "6221e06f-091d-49de-b24d-89aaa67e2f94" }
	 */
	@PostMapping("/toggle")
	public ResponseEntity<CommonResponse<ToggleScrapResponse>> toggleScrap(@RequestBody LogRequest scrapRequest) {
		ToggleScrapResponse toggleResponse = scrapService.toggleScrap(scrapRequest.getNewsId());
		return ResponseEntity.ok(CommonResponse.success(toggleResponse));
	}
}
