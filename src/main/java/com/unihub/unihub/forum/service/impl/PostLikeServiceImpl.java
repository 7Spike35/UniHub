package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.entity.PostLike;
import com.unihub.unihub.forum.repository.PostLikeRepository;
import com.unihub.unihub.forum.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        if (postId == null || userId == null) {
            throw new IllegalArgumentException("帖子ID和用户ID不能为空");
        }

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            return false;
        } else {
            PostLike like = new PostLike(postId, userId);
            postLikeRepository.save(like);
            return true;
        }
    }

    @Override
    public int getLikeCount(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    public boolean isLiked(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
