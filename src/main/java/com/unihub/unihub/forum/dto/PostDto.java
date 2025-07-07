package com.unihub.unihub.forum.dto;

import com.unihub.unihub.forum.entity.PostMedia;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createTime;
    private List<PostMedia> mediaList;

    // 互动数据
    private int likeCount;
    private int favoriteCount;
    private int commentCount;
    private boolean liked;
    private boolean favorited;

    public PostDto() {
    }

    // 更新构造函数以包含新字段
    public PostDto(Long id, Long userId, String username, String content, LocalDateTime createTime, List<PostMedia> mediaList,
                   int likeCount, int favoriteCount, int commentCount, boolean liked, boolean favorited) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.createTime = createTime;
        this.mediaList = mediaList;
        this.likeCount = likeCount;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.liked = liked;
        this.favorited = favorited;
    }
}
