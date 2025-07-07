package com.unihub.unihub.forum.controller;

import com.unihub.unihub.forum.dto.InteractionRequest;
import com.unihub.unihub.forum.service.PostFavoriteService;
import com.unihub.unihub.forum.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostInteractionController {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostFavoriteService postFavoriteService;

    // 点赞/取消点赞
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestBody InteractionRequest request) {
        try {
            boolean isLiked = postLikeService.toggleLike(postId, request.getUserId());
            int likeCount = postLikeService.getLikeCount(postId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("liked", isLiked);
            result.put("likeCount", likeCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 获取点赞状态
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<?> getLikeStatus(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        try {
            boolean isLiked = postLikeService.isLiked(postId, userId);
            int likeCount = postLikeService.getLikeCount(postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isLiked", isLiked,
                    "likeCount", likeCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 收藏/取消收藏
    @PostMapping("/{postId}/favorite")
    public ResponseEntity<?> toggleFavorite(
            @PathVariable Long postId,
            @RequestBody InteractionRequest request) {
        try {
            boolean isFavorited = postFavoriteService.toggleFavorite(postId, request.getUserId());
            int favoriteCount = postFavoriteService.getFavoriteCount(postId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("favorited", isFavorited);
            result.put("favoriteCount", favoriteCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 获取收藏状态
    @GetMapping("/{postId}/favorite/status")
    public ResponseEntity<?> getFavoriteStatus(
            @PathVariable Long postId,
            @RequestParam Long userId) {
        try {
            boolean isFavorited = postFavoriteService.isFavorited(postId, userId);
            int favoriteCount = postFavoriteService.getFavoriteCount(postId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "isFavorited", isFavorited,
                    "favoriteCount", favoriteCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 获取用户收藏的帖子ID列表
    @GetMapping("/favorites")
    public ResponseEntity<?> getUserFavorites(@RequestParam Long userId) {
        try {
            List<Long> favoritePostIds = postFavoriteService.getUserFavoritePostIds(userId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", favoritePostIds
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}
