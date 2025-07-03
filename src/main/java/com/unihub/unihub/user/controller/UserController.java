package com.unihub.unihub.user.controller;

import com.unihub.unihub.common.response.ApiResponse;
import com.unihub.unihub.user.dto.UserLoginDto;
import com.unihub.unihub.user.dto.UserRegisterDto;
import com.unihub.unihub.user.dto.UserUpdateDto;
import com.unihub.unihub.user.entity.UserStatus;
import com.unihub.unihub.user.service.UserService;
import com.unihub.unihub.user.vo.UserVo;
import com.unihub.unihub.forum.entity.Post;
import com.unihub.unihub.forum.repository.PostRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<UserVo> register(@Valid @RequestBody UserRegisterDto registerDto) {
        try {
            UserVo userVo = userService.register(registerDto);
            return ApiResponse.success("注册成功", userVo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            String token = userService.login(loginDto);
            // 获取用户信息
            UserVo userVo = userService.getUserByUsername(loginDto.getUsername());
            
            // 创建返回结果
            Map<String, Object> result = Map.of(
                "token", token,
                "user", userVo
            );
            
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户资料
     */
    @GetMapping("/profile")
    public ApiResponse<UserVo> getCurrentUserProfile(@RequestParam String username) {
        try {
            UserVo userVo = userService.getUserByUsername(username);
            return ApiResponse.success(userVo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            // 根据用户名获取用户ID
            UserVo userVo = userService.getUserByUsername(username);
            userService.changePassword(userVo.getId(), oldPassword, newPassword);
            return ApiResponse.success("密码修改成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<UserVo> getUserById(@PathVariable Long id) {
        try {
            UserVo userVo = userService.getUserById(id);
            return ApiResponse.success(userVo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     */
    @GetMapping("/username/{username}")
    public ApiResponse<UserVo> getUserByUsername(@PathVariable String username) {
        try {
            UserVo userVo = userService.getUserByUsername(username);
            return ApiResponse.success(userVo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ApiResponse<UserVo> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto updateDto) {
        try {
            UserVo userVo = userService.updateUser(id, updateDto);
            return ApiResponse.success("更新成功", userVo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改密码（旧版本，保留兼容性）
     */
    @PutMapping("/{id}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long id, 
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        try {
            userService.changePassword(id, oldPassword, newPassword);
            return ApiResponse.success("密码修改成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 重置密码
     */
    @PutMapping("/{id}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        try {
            userService.resetPassword(id, newPassword);
            return ApiResponse.success("密码重置成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        try {
            userService.updateUserStatus(id, status);
            return ApiResponse.success("状态更新成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户（不分页）
     */
    @GetMapping
    public ApiResponse<List<UserVo>> getAllUsers() {
        try {
            List<UserVo> users = userService.getAllUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<UserVo>> getUserList(
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<UserVo> userPage = userService.getUserList(status, pageable);
            return ApiResponse.success(userPage);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ApiResponse<Page<UserVo>> searchUsers(
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserVo> userPage = userService.searchUsers(status, keyword, pageable);
            return ApiResponse.success(userPage);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = userService.getUserStatistics();
            return ApiResponse.success(statistics);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch-status")
    public ApiResponse<Void> batchUpdateUserStatus(@RequestParam List<Long> userIds, 
                                                  @RequestParam UserStatus status) {
        try {
            userService.batchUpdateUserStatus(userIds, status);
            return ApiResponse.success("批量更新成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> isUsernameAvailable(@RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            return ApiResponse.success(available);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> isEmailAvailable(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            return ApiResponse.success(available);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查学号是否可用
     */
    @GetMapping("/check-student-id")
    public ApiResponse<Boolean> isStudentIdAvailable(@RequestParam String studentId) {
        try {
            boolean available = userService.isStudentIdAvailable(studentId);
            return ApiResponse.success(available);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取指定用户发帖列表
     */
    @GetMapping("/{id}/posts")
    public ApiResponse<List<Post>> getUserPosts(@PathVariable Long id) {
        try {
            List<Post> posts = postRepository.findByUserId(id);
            return ApiResponse.success(posts);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
} 