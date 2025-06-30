package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.detect;
import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;
import com.unihub.unihub.forum.repository.PostRepository;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostMediaRepository postMediaRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Post createPost(Long userId, String content, List<MultipartFile> mediaFiles) {
        // 敏感词检测
        if (detect.containsSensitiveWord(content)) {
            throw new RuntimeException("内容包含敏感词，无法发布");
        }
        // 保存帖子
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setStatus("PASSED");
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        post = postRepository.save(post);
        // 保存媒体
        if (mediaFiles != null) {
            for (MultipartFile file : mediaFiles) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String filePath = "upload/" + fileName;
                try {
                    file.transferTo(new java.io.File(filePath));
                } catch (Exception e) {
                    throw new RuntimeException("文件上传失败");
                }
                String type = file.getContentType() != null && file.getContentType().startsWith("image") ? "image" : "video";
                PostMedia media = new PostMedia();
                media.setPostId(post.getId());
                media.setUrl("/upload/" + fileName);
                media.setType(type);
                media.setCreateTime(LocalDateTime.now());
                postMediaRepository.save(media);
            }
        }
        // 用户发帖数+1
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPostCount(user.getPostCount() + 1);
            userRepository.save(user);
        }
        return post;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
} 