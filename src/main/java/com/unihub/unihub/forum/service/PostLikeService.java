package com.unihub.unihub.forum.service;

public interface PostLikeService {
    /**
     * 点赞或取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return true表示点赞成功，false表示取消点赞
     */
    boolean toggleLike(Long postId, Long userId);
    
    /**
     * 获取帖子的点赞数
     * @param postId 帖子ID
     * @return 点赞数
     */
    int getLikeCount(Long postId);
    
    /**
     * 检查用户是否已点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean isLiked(Long postId, Long userId);
}
