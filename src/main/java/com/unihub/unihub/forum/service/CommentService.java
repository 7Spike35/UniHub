package com.unihub.unihub.forum.service;

import com.unihub.unihub.forum.dto.CommentDto;
import java.util.List;

public interface CommentService {
    /**
     * 添加评论
     * @param postId 帖子ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID（可选，用于回复）
     * @return 评论信息
     */
    CommentDto addComment(Long postId, Long userId, String content, Long parentId);
    
    /**
     * 获取帖子的评论列表（树形结构）
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<CommentDto> getCommentsByPostId(Long postId);
    
    /**
     * 更新评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @param content 新内容
     * @return 是否成功
     */
    boolean updateComment(Long commentId, Long userId, String content);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);
    
    /**
     * 获取帖子的评论数
     * @param postId 帖子ID
     * @return 评论数
     */
    int getCommentCount(Long postId);
}
