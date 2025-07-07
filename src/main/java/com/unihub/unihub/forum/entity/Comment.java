package com.unihub.unihub.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "parent_id")
    private Long parentId; // For nested comments

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    public Comment() {
        this.createTime = LocalDateTime.now();
    }

    public Comment(Long postId, Long userId, String content, Long parentId) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.parentId = parentId;
        this.createTime = LocalDateTime.now();
    }
}
