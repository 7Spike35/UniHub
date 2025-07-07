package com.unihub.unihub.forum.vo;

import java.util.List;

public class PostDetailVo {
    private Long postId;
    private Long userId;
    private String content;
    private String university;
    private String major;
    private String nickname;
    private String userAvatar;
    private List<com.unihub.unihub.forum.entity.PostMedia> mediaList;
    
    // Interaction counts
    private int likeCount;
    private int favoriteCount;
    private int commentCount;
    
    // User interaction status
    private boolean isLiked;
    private boolean isFavorited;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}