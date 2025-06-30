package com.unihub.unihub.forum.repository;

import com.unihub.unihub.forum.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    List<PostMedia> findByPostId(Long postId);
} 