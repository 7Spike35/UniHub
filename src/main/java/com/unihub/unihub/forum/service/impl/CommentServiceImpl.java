package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.dto.CommentDto;
import com.unihub.unihub.forum.entity.Comment;
import com.unihub.unihub.forum.repository.CommentRepository;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.forum.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto addComment(Long postId, Long userId, String content, Long parentId) {
        if (postId == null || userId == null || content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("参数不完整");
        }

        if (parentId != null) {
            commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("父评论不存在"));
        }

        Comment comment = new Comment(postId, userId, content, parentId);
        comment = commentRepository.save(comment);

        com.unihub.unihub.user.entity.User user = userRepository.findById(userId)
                .orElse(null);

        return convertToDto(comment, user);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByCreateTimeDesc(postId);

        Set<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .collect(Collectors.toSet());

                Map<Long, com.unihub.unihub.user.entity.User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(com.unihub.unihub.user.entity.User::getId, user -> user));

        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> rootComments = new ArrayList<>();

        for (Comment comment : comments) {
            com.unihub.unihub.user.entity.User user = userMap.get(comment.getUserId());
            CommentDto dto = convertToDto(comment, user);
            commentMap.put(comment.getId(), dto);
        }

        for (Comment comment : comments) {
            CommentDto dto = commentMap.get(comment.getId());
            if (comment.getParentId() == null) {
                rootComments.add(dto);
            } else {
                CommentDto parentDto = commentMap.get(comment.getParentId());
                if (parentDto != null) {
                    parentDto.addReply(dto);
                }
            }
        }

        return rootComments;
    }

    @Override
    @Transactional
    public boolean updateComment(Long commentId, Long userId, String content) {
        if (commentId == null || userId == null || content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("参数不完整");
        }

        int updated = commentRepository.updateComment(commentId, userId, content);
        return updated > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            throw new IllegalArgumentException("参数不完整");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("无权删除该评论");
        }

        deleteCommentAndReplies(commentId);
        return true;
    }

    @Override
    public int getCommentCount(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子ID不能为空");
        }
        return commentRepository.countByPostId(postId);
    }

    private void deleteCommentAndReplies(Long commentId) {
        List<Comment> replies = commentRepository.findByParentIdOrderByCreateTime(commentId);
        for (Comment reply : replies) {
            deleteCommentAndReplies(reply.getId());
        }
        commentRepository.deleteById(commentId);
    }

    private CommentDto convertToDto(Comment comment, com.unihub.unihub.user.entity.User user) {
        CommentDto dto = new CommentDto();
        BeanUtils.copyProperties(comment, dto);
        if (user != null) {
            dto.setUsername(user.getUsername() != null ? user.getUsername() : "用户" + user.getId());
            dto.setUserAvatar(user.getAvatarUrl() != null ? user.getAvatarUrl() : "/images/default-avatar.png");
        } else {
            dto.setUsername("未知用户");
            dto.setUserAvatar("/images/default-avatar.png");
        }
        return dto;
    }
}
