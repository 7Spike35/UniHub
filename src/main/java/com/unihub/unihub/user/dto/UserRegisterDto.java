package com.unihub.unihub.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.unihub.unihub.user.entity.UserRole;

/**
 * 用户注册DTO
 */
@Getter
@Setter
public class UserRegisterDto {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @NotBlank(message = "学号不能为空")
    @Size(max = 20, message = "学号长度不能超过20个字符")
    private String studentId;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phoneNumber;

    @NotBlank(message = "大学不能为空")
    @Size(max = 100, message = "大学名称长度不能超过100个字符")
    private String university;

    @NotBlank(message = "专业不能为空")
    @Size(max = 100, message = "专业名称长度不能超过100个字符")
    private String major;

    @NotBlank(message = "年级不能为空")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;

    @NotBlank(message = "性别不能为空")
    private String gender;

    private String avatarUrl;

    private UserRole role;
} 