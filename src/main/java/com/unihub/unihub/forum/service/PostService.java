package com.unihub.unihub.forum.service;

import org.springframework.web.multipart.MultipartFile;
import com.unihub.unihub.forum.entity.Post;
import java.util.List;
import com.unihub.unihub.forum.vo.PostDetailVo;

public interface PostService {
    Post createPost(Long userId, String content, List<MultipartFile> mediaFiles);
    List<Post> getAllPosts();
    PostDetailVo getPostDetail(Long postId);
    void deletePost(Long postId, Long operatorUserId);
} 