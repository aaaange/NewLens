package com.ssafy.staticsserver.domain.user.entity;

import com.ssafy.staticsserver.domain.user.enums.ScrapType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
