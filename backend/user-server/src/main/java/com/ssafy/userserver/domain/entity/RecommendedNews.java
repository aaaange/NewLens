package com.ssafy.userserver.domain.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recommended_news", indexes = {@Index(name = "idx_recommended_news_user_id", columnList = "user_id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedNews {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "news_id", nullable = false)
	@JsonProperty("news_id")
	private String newsId;

	@Column(name = "recommended_at", nullable = false)
	@JsonProperty("recommended_at")
	private LocalDateTime recommendedAt;

	@Column(name = "recommend_count", nullable = false)
	@JsonProperty("recommend_count")
	private int recommendCount;

	public RecommendedNews(User user, String newsId, LocalDateTime recommendedAt, int recommendCount) {
		this.user = user;
		this.newsId = newsId;
		this.recommendedAt = recommendedAt;
		this.recommendCount = recommendCount;
	}

	public static RecommendedNews of(User user, String newsId, LocalDateTime recommendedAt) {
		return new RecommendedNews(user, newsId, recommendedAt, 1);
	}

	public void incrementRecommendCount() {
		this.recommendCount++;
	}
}
