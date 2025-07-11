package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.entity.PostFavorite;
import com.unihub.unihub.forum.repository.PostFavoriteRepository;
import com.unihub.unihub.forum.service.PostFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.unihub.unihub.forum.dto.PostDto;
import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.repository.PostRepository;
import com.unihub.unihub.user.entity.User;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;

@Service
public class PostFavoriteServiceImpl implements PostFavoriteService {

    @Autowired
    private PostFavoriteRepository postFavoriteRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostMediaRepository postMediaRepository;

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

    @Override
    public List<PostDto> getFavoritePostsByUser(Long userId) {
        List<PostFavorite> favorites = postFavoriteRepository.findByUserId(userId);
        List<PostDto> result = new java.util.ArrayList<>();
        for (PostFavorite fav : favorites) {
            Post post = postRepository.findById(fav.getPostId()).orElse(null);
            if (post != null) {
                User user = userRepository.findById(post.getUserId()).orElse(new User());
                List<PostMedia> mediaList = postMediaRepository.findByPostId(post.getId());
                PostDto dto = new PostDto(
                    post.getId(),
                    post.getUserId(),
                    user.getUsername(),
                    post.getContent(),
                    post.getCreateTime(),
                    mediaList,
                    0, 0, 0, false, true // 收藏列表无需点赞等数据
                );
                result.add(dto);
            }
        }
        return result;
    }
}
