package com.unihub.unihub.forum.controller;

import com.unihub.unihub.forum.dto.CommentCreationRequest;
import com.unihub.unihub.forum.dto.CommentDto;
import com.unihub.unihub.forum.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentCreationRequest request) {
        try {
            CommentDto newComment = commentService.addComment(
                request.getPostId(),
                request.getUserId(),
                request.getContent(),
                request.getParentId()
            );
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "评论成功",
                "data", newComment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "评论失败: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getCommentsByPostId(@RequestParam Long postId) {
        try {
            List<CommentDto> comments = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "获取评论失败: " + e.getMessage()
            ));
        }
    }
}
