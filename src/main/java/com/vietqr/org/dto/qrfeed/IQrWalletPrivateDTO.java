package com.vietqr.org.dto.qrfeed;

public interface IQrWalletPrivateDTO {
    String getId();
    String getTitle();
    String getDescription();
    String getValue();
    String getQrType();
    long getTimeCreated();
    String getUserId();

    String getData();

    String getFullName();
    String getImageId();
    String getStyle();
    String getTheme();

    String getFileAttachmentId();
}
