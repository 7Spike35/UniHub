package com.unihub.unihub.forum.repository;

import com.unihub.unihub.forum.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    int countByPostId(Long postId);
    
    List<PostLike> findByPostId(Long postId);
    
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
