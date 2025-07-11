package com.unihub.unihub.forum.service;

import java.util.List;
import com.unihub.unihub.forum.dto.PostDto;

public interface PostFavoriteService {
    /**
     * 收藏或取消收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return true表示收藏成功，false表示取消收藏
     */
    boolean toggleFavorite(Long postId, Long userId);
    
    /**
     * 获取帖子的收藏数
     * @param postId 帖子ID
     * @return 收藏数
     */
    int getFavoriteCount(Long postId);
    
    /**
     * 检查用户是否已收藏
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean isFavorited(Long postId, Long userId);
    
    /**
     * 获取用户收藏的帖子ID列表
     * @param userId 用户ID
     * @return 收藏的帖子ID列表
     */
    List<Long> getUserFavoritePostIds(Long userId);

    /**
     * 获取用户收藏的帖子
     * @param userId 用户ID
     * @return 收藏的帖子列表
     */
    List<PostDto> getFavoritePostsByUser(Long userId);
}
