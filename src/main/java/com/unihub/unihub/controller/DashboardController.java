package com.unihub.unihub.controller;

import com.unihub.unihub.common.response.ApiResponse;
import com.unihub.unihub.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表板控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserService userService;

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        try {
            Map<String, Object> userStats = userService.getUserStatistics();
            
            // 创建仪表板统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", userStats.get("totalUsers"));
            stats.put("activeUsers", userStats.get("activeUsers"));
            stats.put("resources", 0); // 待实现
            stats.put("todayEvents", 0); // 待实现
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
} 