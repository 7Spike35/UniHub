package com.unihub.unihub.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_media")
@Getter
@Setter
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "url", length = 255, nullable = false)
    private String url;

    @Column(name = "type", length = 20, nullable = false)
    private String type; // image/video

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    public PostMedia() {
        this.createTime = LocalDateTime.now();
    }
} 