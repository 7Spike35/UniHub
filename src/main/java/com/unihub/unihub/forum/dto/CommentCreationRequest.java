package com.unihub.unihub.forum.dto;

import lombok.Data;

@Data
public class CommentCreationRequest {
    private Long postId;
    private Long userId;
    private String content;
    private Long parentId; // For replies
}
