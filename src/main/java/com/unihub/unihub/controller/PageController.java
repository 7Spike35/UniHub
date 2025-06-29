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
} 