package com.unihub.unihub.forum.repository;

import com.unihub.unihub.forum.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
} 