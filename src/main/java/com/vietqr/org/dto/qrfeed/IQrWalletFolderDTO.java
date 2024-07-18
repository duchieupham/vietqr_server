package com.vietqr.org.dto.qrfeed;

public interface IQrWalletFolderDTO {
    String getId();
    String getTitle();
    String getDescription();
    String getValue();
    String getQrType();
    long getTimeCreated();
    String getData();
    String getFullName();
    String getImageId();
    String getStyle();
    String getTheme();
    String getFileAttachmentId();
    Integer getAddedToFolder();
}