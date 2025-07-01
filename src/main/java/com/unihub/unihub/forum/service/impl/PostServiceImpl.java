package com.unihub.unihub.forum.service.impl;

import com.unihub.unihub.forum.detect;
import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.entity.PostMedia;
import com.unihub.unihub.forum.repository.PostMediaRepository;
import com.unihub.unihub.forum.repository.PostRepository;
import com.unihub.unihub.forum.service.PostService;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.user.entity.User;
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
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
} 