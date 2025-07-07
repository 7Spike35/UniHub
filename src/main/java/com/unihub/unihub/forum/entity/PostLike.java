package com.unihub.unihub.forum.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 帖子点赞实体类
 */
@Entity
@Table(name = "post_like")
@Getter
@Setter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    public PostLike() {
        this.createTime = LocalDateTime.now();
    }

    public PostLike(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
        this.createTime = LocalDateTime.now();
    }
}
