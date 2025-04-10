package com.ssafy.searchserver.infrastructure.scheduler;

import com.ssafy.searchserver.application.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class KeywordRankingScheduler {

	private final SearchService searchService;

	/**
	 * 매일 새벽 4시에 키워드 랭킹 통계 요청을 실행합니다.
	 */
//	 @Scheduled(cron = "00 55 03 * * ?") // 03:55분으로 한국 특화 정각 피하기 위해
	public void scheduleKeywordRanking() {
		List<String> categories = Arrays.asList(
			"general", "science", "sports", "business", "health",
			"entertainment", "tech", "politics", "food", "travel", "all"
		);
		List<Integer> periods = Arrays.asList(1, 7, 30);
		List<Boolean> isKoreaList = Arrays.asList(true, false);

		for (String category : categories) {
			for (Integer period : periods) {
				for (Boolean isKorea : isKoreaList) {
					try {
						log.info("키워드 랭킹 요청 - category: {}, period: {}, isKorea: {}", category, period, isKorea);
						searchService.triggerKeywordRanking(category, period, isKorea);
					} catch (Exception e) {
						log.error("키워드 랭킹 요청 실패 - category: {}, period: {}, isKorea: {}", category, period, isKorea, e);
					}
				}
			}
		}
	}


//	@Scheduled(cron = "0 0 * * * ?") // 정각 마다
	@Scheduled(cron = "20 10 02 * * ?")
	public void scheduleHourlyKoreaRanking() {
		List<String> categories = Arrays.asList(
			"general", "science", "sports", "business", "health",
			"entertainment", "tech", "politics", "food", "travel", "all"
		);

		for (String category : categories) {
			try {
				searchService.triggerHourlyKeywordRanking(category, true);
			} catch (Exception e) {
				log.error("실시간 키워드 랭킹 실패: {}", category, e);
			}
		}

	}
}
