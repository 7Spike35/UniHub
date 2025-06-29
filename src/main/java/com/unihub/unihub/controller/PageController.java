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
     * 用户管理页面
     */
    @GetMapping("/user-management")
    public String userManagement() {
        return "user-management";
    }

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/user-management";
    }
} 