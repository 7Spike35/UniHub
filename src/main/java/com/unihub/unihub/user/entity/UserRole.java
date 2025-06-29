package com.unihub.unihub.user.entity;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    STUDENT("学生"),
    TEACHER("教师"),
    ADMIN("管理员"),
    MODERATOR("版主");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
} 