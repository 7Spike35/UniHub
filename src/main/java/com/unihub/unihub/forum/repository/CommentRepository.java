package com.unihub.unihub.forum.repository;

import com.unihub.unihub.forum.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPostIdOrderByCreateTimeDesc(Long postId);
    
    List<Comment> findByPostIdAndParentIdIsNullOrderByCreateTimeDesc(Long postId);
    
    List<Comment> findByParentIdOrderByCreateTime(Long parentId);
    
    int countByPostId(Long postId);
    
    @Modifying
    @Query("UPDATE Comment c SET c.content = :content, c.updateTime = CURRENT_TIMESTAMP WHERE c.id = :id AND c.userId = :userId")
    int updateComment(@Param("id") Long id, @Param("userId") Long userId, @Param("content") String content);
    
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :id AND c.userId = :userId")
    int deleteComment(@Param("id") Long id, @Param("userId") Long userId);
}
