package com.unihub.unihub.forum.service;

import org.springframework.web.multipart.MultipartFile;
import com.unihub.unihub.forum.entity.Post;
import java.util.List;
import com.unihub.unihub.forum.dto.PostDto;
import com.unihub.unihub.forum.vo.PostDetailVo;

public interface PostService {
    Post createPost(Long userId, String content, List<MultipartFile> mediaFiles);
    List<PostDto> getAllPosts(Long currentUserId);
    PostDetailVo getPostDetail(Long postId);
    PostDetailVo getPostDetail(Long postId, Long currentUserId);
    void deletePost(Long postId, Long operatorUserId);
    /**
     * 通过ID列表批量获取PostDto
     */
    List<PostDto> getPostsByIds(List<Long> ids, Long currentUserId);
} 