package com.unihub.unihub.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_favorite")
@Getter
@Setter
public class PostFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    public PostFavorite() {
        this.createTime = LocalDateTime.now();
    }

    public PostFavorite(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
        this.createTime = LocalDateTime.now();
    }
}
