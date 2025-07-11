package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.detect;
import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;
import com.unihub.unihub.forum.repository.PostRepository;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.forum.service.PostLikeService;
import com.unihub.unihub.forum.service.PostFavoriteService;
import com.unihub.unihub.forum.service.CommentService;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.user.entity.User;
import com.unihub.unihub.forum.dto.PostDto;
import com.unihub.unihub.forum.vo.PostDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class PostServiceImpl implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    @Value("${file.upload-dir:upload}")
    private String uploadDir;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMediaRepository postMediaRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostLikeService postLikeService;
    
    @Autowired
    private PostFavoriteService postFavoriteService;
    
    @Autowired
    private CommentService commentService;

    @Override
    public Post createPost(Long userId, String content, List<MultipartFile> mediaFiles) {
        // 敏感词检测
        if (detect.containsSensitiveWord(content)) {
            throw new RuntimeException("内容包含敏感词，无法发布");
        }

        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            // 保存帖子
            Post post = new Post();
            post.setUserId(userId);
            post.setContent(content);
            post.setStatus("PASSED");
            post.setCreateTime(LocalDateTime.now());
            post.setUpdateTime(LocalDateTime.now());
            post = postRepository.save(post);

            // 处理文件上传
            if (mediaFiles != null && !mediaFiles.isEmpty()) {
                List<PostMedia> savedMedia = new ArrayList<>();

                for (MultipartFile file : mediaFiles) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }

                    // 生成唯一文件名
                    String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
                    String fileExtension = "";
                    int lastDotIndex = originalFilename.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        fileExtension = originalFilename.substring(lastDotIndex);
                    }
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

                    // 创建目标文件
                    File uploadDirFile = new File(uploadDir);
                    
                    // 确保上传目录存在
                    if (!uploadDirFile.exists()) {
                        boolean dirsCreated = uploadDirFile.mkdirs();
                        if (!dirsCreated) {
                            // 如果创建目录失败，尝试使用绝对路径
                            uploadDirFile = new File(System.getProperty("user.dir"), uploadDir);
                            if (!uploadDirFile.exists()) {
                                dirsCreated = uploadDirFile.mkdirs();
                                if (!dirsCreated) {
                                    throw new RuntimeException("无法创建上传目录: " + uploadDirFile.getAbsolutePath());
                                }
                            }
                        }
                    }
                    
                    // 使用规范路径来避免路径问题
                    String canonicalPath = uploadDirFile.getCanonicalPath();
                    File targetFile = new File(canonicalPath, uniqueFileName);
                    
                    // 记录上传路径信息
                    logger.info("准备保存文件到: {}", targetFile.getAbsolutePath());

                    try {
                        // 保存文件
                        file.transferTo(targetFile);

                        // 保存媒体信息
                        String contentType = file.getContentType();
                        String mediaType = (contentType != null && contentType.startsWith("image")) ? "image" : "video";

                        // 使用相对URL路径，不包含文件系统路径
                        String fileUrl = "/upload/" + uniqueFileName;
                        
                        PostMedia media = new PostMedia();
                        media.setPostId(post.getId());
                        media.setUrl(fileUrl);
                        media.setType(mediaType);
                        media.setCreateTime(LocalDateTime.now());
                        savedMedia.add(postMediaRepository.save(media));
                        
                        logger.info("文件已保存，访问URL: {}", fileUrl);

                        logger.info("文件上传成功: {}", targetFile.getAbsolutePath());

                    } catch (IOException e) {
                        logger.error("文件保存失败: {}", e.getMessage(), e);
                        throw new RuntimeException("文件保存失败: " + e.getMessage());
                    }
                }

                // 更新帖子关联的媒体文件
                // Post实体中没有mediaCount字段，暂时注释掉这行
                // post.setMediaCount(savedMedia.size());
                post = postRepository.save(post);
            }

            // 用户发帖数+1
            user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPostCount(user.getPostCount() + 1);
                userRepository.save(user);
            }
            return post;
        } catch (Exception e) {
            logger.error("创建帖子失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建帖子失败: " + e.getMessage());
        }
    }

    @Override
    public List<PostDto> getAllPosts(Long currentUserId) {
        List<Post> posts = postRepository.findAll();
        List<PostDto> dtos = new ArrayList<>();
        for (Post post : posts) {
            User user = userRepository.findById(post.getUserId()).orElse(new User());
            List<PostMedia> mediaList = postMediaRepository.findByPostId(post.getId());

            int likeCount = postLikeService.getLikeCount(post.getId());
            int favoriteCount = postFavoriteService.getFavoriteCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean isLiked = currentUserId != null && postLikeService.isLiked(post.getId(), currentUserId);
            boolean isFavorited = currentUserId != null && postFavoriteService.isFavorited(post.getId(), currentUserId);

            PostDto dto = new PostDto(
                post.getId(),
                post.getUserId(),
                user.getUsername(),
                post.getContent(),
                post.getCreateTime(),
                mediaList,
                likeCount,
                favoriteCount,
                commentCount,
                isLiked,
                isFavorited
            );
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public PostDetailVo getPostDetail(Long postId) {
        return getPostDetail(postId, null);
    }
    
    @Override
    public PostDetailVo getPostDetail(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User user = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        List<PostMedia> mediaList = postMediaRepository.findByPostId(postId);
        
        // 获取互动数据
        int likeCount = postLikeService.getLikeCount(postId);
        int favoriteCount = postFavoriteService.getFavoriteCount(postId);
        int commentCount = commentService.getCommentCount(postId);
        
        // 检查当前用户是否已点赞/收藏
        boolean isLiked = currentUserId != null && postLikeService.isLiked(postId, currentUserId);
        boolean isFavorited = currentUserId != null && postFavoriteService.isFavorited(postId, currentUserId);
        
        PostDetailVo vo = new PostDetailVo();
        vo.setPostId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setContent(post.getContent());
        vo.setNickname(user.getUsername() != null ? user.getUsername() : "用户" + user.getId());
        // 使用用户头像，如果没有则使用默认头像
        String avatar = user.getAvatarUrl() != null ? user.getAvatarUrl() : "/images/default-avatar.png";
        vo.setUserAvatar(avatar);
        vo.setUniversity(user.getUniversity());
        vo.setMajor(user.getMajor());
        vo.setMediaList(mediaList);
        
        // 设置互动数据
        vo.setLikeCount(likeCount);
        vo.setFavoriteCount(favoriteCount);
        vo.setCommentCount(commentCount);
        vo.setLiked(isLiked);
        vo.setFavorited(isFavorited);
        
        return vo;
    }

    @Override
    public void deletePost(Long postId, Long operatorUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User operator = userRepository.findById(operatorUserId)
                .orElseThrow(() -> new RuntimeException("操作用户不存在"));
        // 只有管理员或发帖人本人可删除
        if (!operator.getRole().name().equals("ADMIN") && !Objects.equals(post.getUserId(), operatorUserId)) {
            throw new RuntimeException("无权限删除该帖子");
        }
        // 删除媒体文件
        List<PostMedia> mediaList = postMediaRepository.findByPostId(postId);
        for (PostMedia media : mediaList) {
            postMediaRepository.delete(media);
            // 可选：删除服务器上的实际文件
        }
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByIds(List<Long> ids, Long currentUserId) {
        List<Post> posts = postRepository.findAllById(ids);
        List<PostDto> dtos = new ArrayList<>();
        for (Post post : posts) {
            User user = userRepository.findById(post.getUserId()).orElse(new User());
            List<PostMedia> mediaList = postMediaRepository.findByPostId(post.getId());
            int likeCount = postLikeService.getLikeCount(post.getId());
            int favoriteCount = postFavoriteService.getFavoriteCount(post.getId());
            int commentCount = commentService.getCommentCount(post.getId());
            boolean isLiked = currentUserId != null && postLikeService.isLiked(post.getId(), currentUserId);
            boolean isFavorited = currentUserId != null && postFavoriteService.isFavorited(post.getId(), currentUserId);
            PostDto dto = new PostDto(
                post.getId(),
                post.getUserId(),
                user.getUsername(),
                post.getContent(),
                post.getCreateTime(),
                mediaList,
                likeCount,
                favoriteCount,
                commentCount,
                isLiked,
                isFavorited
            );
            dtos.add(dto);
        }
        return dtos;
    }
} 