package com.ssafy.userserver.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ssafy.userserver.domain.enums.Active;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "profile_image")
	private String profileImage;

	@Enumerated(EnumType.STRING)
	@Column(name = "active", nullable = false)
	private Active active;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Builder
	public User(String nickname, String email, String profileImage, Active active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
