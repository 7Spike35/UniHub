package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.entity.PostFavorite;
import com.unihub.unihub.forum.repository.PostFavoriteRepository;
import com.unihub.unihub.forum.service.PostFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostFavoriteServiceImpl implements PostFavoriteService {

    @Autowired
    private PostFavoriteRepository postFavoriteRepository;

    @Override
    @Transactional
    public boolean toggleFavorite(Long postId, Long userId) {
        if (postId == null || userId == null) {
            throw new IllegalArgumentException("帖子ID和用户ID不能为空");
        }

        if (postFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            postFavoriteRepository.deleteByPostIdAndUserId(postId, userId);
            return false;
        } else {
            PostFavorite favorite = new PostFavorite(postId, userId);
            postFavoriteRepository.save(favorite);
            return true;
        }
    }

    @Override
    public int getFavoriteCount(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }
        return postFavoriteRepository.countByPostId(postId);
    }

    @Override
    public boolean isFavorited(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }
        return postFavoriteRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public List<Long> getUserFavoritePostIds(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return postFavoriteRepository.findByUserId(userId).stream()
                .map(PostFavorite::getPostId)
                .collect(Collectors.toList());
    }
}
