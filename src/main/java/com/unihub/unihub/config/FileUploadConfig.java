package com.unihub.unihub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            boolean created = uploadDirectory.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建上传目录: " + uploadDir);
            }
        }
    }

    @Bean
    public MultipartResolver multipartResolver() {
        // 使用 StandardServletMultipartResolver 替代 CommonsMultipartResolver
        // 文件大小限制在 application.properties 中通过 spring.servlet.multipart 配置
        return new StandardServletMultipartResolver();
    }
}
