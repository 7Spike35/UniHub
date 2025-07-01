package com.unihub.unihub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadDir = "upload";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传目录到 /upload/**
        String uploadPath = Paths.get("").toAbsolutePath().toString() + "/" + uploadDir + "/";
        
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
                
        // 确保静态资源也能被访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
