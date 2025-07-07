package com.unihub.unihub.forum.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String userAvatar;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CommentDto> replies = new ArrayList<>();
    
    // For nested comments
    public void addReply(CommentDto reply) {
        this.replies.add(reply);
    }
}
