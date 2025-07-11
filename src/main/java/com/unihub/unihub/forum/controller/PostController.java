package com.unihub.unihub.forum.controller;

import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.forum.service.PostFavoriteService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

import com.unihub.unihub.forum.dto.PostDto;
import com.unihub.unihub.forum.vo.PostDetailVo;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostFavoriteService postFavoriteService;

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
    public ResponseEntity<?> listPosts(@RequestParam(required = false) Long currentUserId) {
        try {
                        List<PostDto> posts = postService.getAllPosts(currentUserId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", posts
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取帖子列表失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long currentUserId) {
        try {
            PostDetailVo detail = postService.getPostDetail(id, currentUserId);
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

    @GetMapping("/favorites/user/{userId}")
    public ResponseEntity<?> getUserFavorites(@PathVariable Long userId) {
        try {
            List<PostDto> favorites = postFavoriteService.getFavoritePostsByUser(userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", favorites
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取收藏失败: " + e.getMessage()
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