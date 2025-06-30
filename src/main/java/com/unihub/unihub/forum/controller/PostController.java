package com.unihub.unihub.forum.controller;

import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @PostMapping
    public Post createPost(
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> mediaFiles
    ) {
        return postService.createPost(userId, content, mediaFiles);
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
            List<PostMedia> mediaList = postMediaRepository.findByPostId(post.getId());
            map.put("mediaList", mediaList);
            result.add(map);
        }
        return result;
    }
} 