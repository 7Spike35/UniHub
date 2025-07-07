package com.unihub.unihub.forum.controller;

import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;
import com.unihub.unihub.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

import com.unihub.unihub.forum.vo.PostDetailVo;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostMediaRepository postMediaRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPost(
            @RequestParam("userId") Long userId,
            @RequestParam("content") String content,
            @RequestParam(value = "mediaFiles", required = false) List<MultipartFile> mediaFiles) {
        
        try {
            // 确保上传目录存在
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            
            Post post = postService.createPost(userId, content, mediaFiles);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "发帖成功",
                "post", post
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "发帖失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/list")
    public List<Map<String, Object>> listPosts() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Post post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("userId", post.getUserId());
            map.put("content", post.getContent());
            map.put("createTime", post.getCreateTime());
            
            // 获取用户信息
            com.unihub.unihub.user.entity.User user = userRepository.findById(post.getUserId())
                .orElse(new com.unihub.unihub.user.entity.User());
            map.put("nickname", user.getUsername() != null ? user.getUsername() : "用户" + post.getUserId());
            
            // 获取媒体文件
            List<PostMedia> mediaList = postMediaRepository.findByPostId(post.getId());
            map.put("mediaList", mediaList);
            
            result.add(map);
        }
        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
        try {
            PostDetailVo detail = postService.getPostDetail(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", detail
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestParam Long operatorUserId) {
        try {
            postService.deletePost(id, operatorUserId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 