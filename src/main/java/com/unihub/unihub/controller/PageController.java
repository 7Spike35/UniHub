package com.unihub.unihub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面控制器
 * 处理前端页面的路由
 */
@Controller
public class PageController {

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 系统主页（仪表板）
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/user-management")
    public String userManagement() {
        return "user-management";
    }

    /**
     * 首页 - 重定向到登录页面
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * 讨论区页面
     */
    @GetMapping("/forum")
    public String forum() {
        return "forum";
    }

    /**
     * 发帖页面
     */
    @GetMapping("/post-create")
    public String postCreate() {
        return "post-create";
    }

    /**
     * 帖子详情页面
     */
    @GetMapping("/post-detail/{id}")
    public String postDetail() {
        return "post-detail";
    }
} 