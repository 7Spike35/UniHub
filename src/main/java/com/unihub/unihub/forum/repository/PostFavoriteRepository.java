package com.unihub.unihub.forum.repository;

import com.unihub.unihub.forum.entity.PostFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {
    
    Optional<PostFavorite> findByPostIdAndUserId(Long postId, Long userId);
    
    int countByPostId(Long postId);
    
    List<PostFavorite> findByUserId(Long userId);
    
    void deleteByPostIdAndUserId(Long postId, Long userId);
    
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
