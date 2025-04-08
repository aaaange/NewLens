package com.ssafy.userserver.domain.entity;

import java.time.LocalDateTime;

import com.ssafy.userserver.domain.enums.ProviderName;

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
@Table(name = "user_providers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProvider {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "provider_name", nullable = false)
	private ProviderName providerName;

	@Column(name = "provider_id", nullable = false)
	private String providerId;

	@Column(name = "access_token", columnDefinition = "TEXT")
	private String accessToken;

	@Column(name = "refresh_token", columnDefinition = "TEXT")
	private String refreshToken;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public UserProvider(User user, ProviderName providerName, String providerId, String accessToken, String refreshToken, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.user = user;
		this.providerName = providerName;
		this.providerId = providerId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
