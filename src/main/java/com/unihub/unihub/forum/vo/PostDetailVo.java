package com.unihub.unihub.forum.vo;

import java.util.List;

public class PostDetailVo {
    private Long postId;
    private String content;
    private String university;
    private String major;
    private String nickname;
    private List<com.unihub.unihub.forum.entity.PostMedia> mediaList;

    public Long getPostId() {
        return postId;
    }
    public void setPostId(Long postId) {
        this.postId = postId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
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
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public List<com.unihub.unihub.forum.entity.PostMedia> getMediaList() {
        return mediaList;
    }
    public void setMediaList(List<com.unihub.unihub.forum.entity.PostMedia> mediaList) {
        this.mediaList = mediaList;
    }
} 