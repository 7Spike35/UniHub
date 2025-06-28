package com.unihub.unihub.user.dto;

import jakarta.validation.constraints.*;

/**
 * 用户信息更新DTO
 */
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

    // Getters and Setters
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
} 