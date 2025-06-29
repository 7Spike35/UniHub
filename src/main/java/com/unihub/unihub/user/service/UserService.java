package com.unihub.unihub.user.service;

import com.unihub.unihub.user.dto.UserLoginDto;
import com.unihub.unihub.user.dto.UserRegisterDto;
import com.unihub.unihub.user.dto.UserUpdateDto;
import com.unihub.unihub.user.entity.User;
import com.unihub.unihub.user.entity.UserStatus;
import com.unihub.unihub.user.vo.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    UserVo register(UserRegisterDto registerDto);
    
    /**
     * 用户登录
     */
    String login(UserLoginDto loginDto);
    
    /**
     * 根据ID获取用户信息
     */
    UserVo getUserById(Long id);
    
    /**
     * 根据用户名获取用户信息
     */
    UserVo getUserByUsername(String username);
    
    /**
     * 更新用户信息
     */
    UserVo updateUser(Long userId, UserUpdateDto updateDto);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);
    
    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, UserStatus status);
    
    /**
     * 删除用户
     */
    void deleteUser(Long userId);
    
    /**
     * 分页查询用户列表
     */
    Page<UserVo> getUserList(UserStatus status, Pageable pageable);
    
    /**
     * 获取所有用户（不分页）
     */
    List<UserVo> getAllUsers();
    
    /**
     * 搜索用户
     */
    Page<UserVo> searchUsers(UserStatus status, String keyword, Pageable pageable);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 批量更新用户状态
     */
    void batchUpdateUserStatus(List<Long> userIds, UserStatus status);
    
    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 检查邮箱是否可用
     */
    boolean isEmailAvailable(String email);
    
    /**
     * 检查学号是否可用
     */
    boolean isStudentIdAvailable(String studentId);
} 