package com.ssafy.userserver.domain.entity;

import java.time.LocalDateTime;

import com.ssafy.userserver.domain.enums.ScrapType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "scraps")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private ScrapType type;

	@Column(name = "news_id", nullable = false)
	private String newsId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Scrap(User user, ScrapType type, String newsId, LocalDateTime createdAt) {
		this.user = user;
		this.type = type;
		this.newsId = newsId;
		this.createdAt = createdAt;
	}
}
