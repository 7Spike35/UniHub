package com.unihub.unihub.user.service.impl;

import com.unihub.unihub.user.dto.UserLoginDto;
import com.unihub.unihub.user.dto.UserRegisterDto;
import com.unihub.unihub.user.dto.UserUpdateDto;
import com.unihub.unihub.user.entity.User;
import com.unihub.unihub.user.entity.UserRole;
import com.unihub.unihub.user.entity.UserStatus;
import com.unihub.unihub.user.repository.UserRepository;
import com.unihub.unihub.user.service.UserService;
import com.unihub.unihub.user.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVo register(UserRegisterDto registerDto) {
        // 验证密码确认
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 检查学号是否已存在
        if (userRepository.existsByStudentId(registerDto.getStudentId())) {
            throw new RuntimeException("学号已被注册");
        }

        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(registerDto, user);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.STUDENT);

        User savedUser = userRepository.save(user);
        return convertToVo(savedUser);
    }

    @Override
    public String login(UserLoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("账户状态异常，请联系管理员");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // TODO: 生成JWT token
        return "登录成功，token待实现";
    }

    @Override
    public UserVo getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVo(userOpt.get());
    }

    @Override
    public UserVo getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVo(userOpt.get());
    }

    @Override
    public UserVo updateUser(Long userId, UserUpdateDto updateDto) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        
        // 更新非空字段
        if (updateDto.getRealName() != null) {
            user.setRealName(updateDto.getRealName());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDto.getAvatarUrl());
        }
        if (updateDto.getUniversity() != null) {
            user.setUniversity(updateDto.getUniversity());
        }
        if (updateDto.getMajor() != null) {
            user.setMajor(updateDto.getMajor());
        }
        if (updateDto.getGrade() != null) {
            user.setGrade(updateDto.getGrade());
        }

        User savedUser = userRepository.save(user);
        return convertToVo(savedUser);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void updateUserStatus(Long userId, UserStatus status) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Page<UserVo> getUserList(UserStatus status, Pageable pageable) {
        Page<User> userPage = userRepository.findByStatus(status, pageable);
        return userPage.map(this::convertToVo);
    }

    @Override
    public Page<UserVo> searchUsers(UserStatus status, String keyword, Pageable pageable) {
        Page<User> userPage = userRepository.searchUsers(status, keyword, pageable);
        return userPage.map(this::convertToVo);
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数
        long totalUsers = userRepository.count();
        statistics.put("totalUsers", totalUsers);
        
        // 各状态用户数量
        List<Object[]> statusCounts = userRepository.countUsersByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] result : statusCounts) {
            UserStatus status = (UserStatus) result[0];
            Long count = (Long) result[1];
            statusMap.put(status.name(), count);
        }
        statistics.put("statusCounts", statusMap);
        
        return statistics;
    }

    @Override
    public void batchUpdateUserStatus(List<Long> userIds, UserStatus status) {
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            user.setStatus(status);
        }
        userRepository.saveAll(users);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public boolean isStudentIdAvailable(String studentId) {
        return !userRepository.existsByStudentId(studentId);
    }

    /**
     * 将User实体转换为UserVo
     */
    private UserVo convertToVo(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }
} 