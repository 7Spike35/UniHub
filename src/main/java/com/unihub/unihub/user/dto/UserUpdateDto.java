package com.unihub.unihub.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息更新DTO
 */
@Getter
@Setter
public class UserUpdateDto {
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    @Size(max = 100, message = "大学名称长度不能超过100个字符")
    private String university;

    @Size(max = 100, message = "专业名称长度不能超过100个字符")
    private String major;

    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;
} 