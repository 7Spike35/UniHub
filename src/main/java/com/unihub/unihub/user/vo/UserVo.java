package com.unihub.unihub.user.vo;

import com.unihub.unihub.user.entity.UserRole;
import com.unihub.unihub.user.entity.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户视图对象
 */
@Getter
@Setter
public class UserVo {
    private Long id;
    private String username;
    private String email;
    private String realName;
    private String studentId;
    private String phoneNumber;
    private String avatarUrl;
    private String university;
    private String major;
    private String grade;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdTime;
    private LocalDateTime lastLoginTime;
} 