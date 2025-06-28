package com.unihub.unihub.user.entity;

/**
 * 用户状态枚举
 */
public enum UserStatus {
    ACTIVE("活跃"),
    INACTIVE("非活跃"),
    BANNED("封禁"),
    PENDING("待审核");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 