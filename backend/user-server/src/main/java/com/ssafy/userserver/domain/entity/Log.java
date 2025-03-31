package com.ssafy.userserver.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "news_id")
	private String newsId;

	@Column(name = "visited_at")
	private LocalDateTime visitedAt;

	@Builder
	public Log(User user, String newsId, LocalDateTime visitedAt) {
		this.user = user;
		this.newsId = newsId;
		this.visitedAt = visitedAt;
	}
}
