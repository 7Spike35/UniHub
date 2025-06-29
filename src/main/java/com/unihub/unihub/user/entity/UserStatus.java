package com.unihub.unihub.user.entity;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatus {
    ACTIVE("活跃"),
    INACTIVE("非活跃"),
    BANNED("封禁"),
    PENDING("待审核");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
} 