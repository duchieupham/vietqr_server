package com.vietqr.org.dto.qrfeed;

import java.util.List;

public class QrWalletDetailDTO {
    private String id;
    private String title;
    private String description;
    private String value; // URL của QR hoặc thông tin vCard
    private String qrType; // Loại QR như "QR Link" hoặc "VCard"
    private long timeCreated; // Thời gian tạo
    private String userId; // ID người dùng tạo QR
    private int likeCount; // Số lượt thích
    private int commentCount; // Số lượt bình luận
    private int hasLiked; // Người dùng đã thích chưa
    private String data; // Dữ liệu thêm vào
    private List<QrCommentDTO> comments; // Danh sách các bình luận


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<QrCommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<QrCommentDTO> comments) {
        this.comments = comments;
    }

    public int getHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(int hasLiked) {
        this.hasLiked = hasLiked;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
