package com.vietqr.org.dto.qrfeed;

public interface IQrWalletDTO {
    String getId();
    String getTitle();
    String getDescription();
    String getValue();
    String getQrType();
    long getTimeCreated();
    String getUserId();
    int getLikeCount();
    int getCommentCount();
    boolean getHasLiked();
}
