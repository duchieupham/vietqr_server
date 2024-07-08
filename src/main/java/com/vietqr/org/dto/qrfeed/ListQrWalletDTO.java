package com.vietqr.org.dto.qrfeed;

public class ListQrWalletDTO {
    private String id;
    private String description;
    private String isPublic;
    private String qrType;
    private String timeCreate;
    private String title;
    private String content;
    private String data;
    private String vlue;
    private String fileAttachmentId;

    public ListQrWalletDTO(String id, String description, String isPublic, String qrType, String timeCreate, String title, String content, String data, String vlue, String fileAttachmentId) {
        this.id = id;
        this.description = description;
        this.isPublic = isPublic;
        this.qrType = qrType;
        this.timeCreate = timeCreate;
        this.title = title;
        this.content = content;
        this.data = data;
        this.vlue = vlue;
        this.fileAttachmentId = fileAttachmentId;
    }

    public String getVlue() {
        return vlue;
    }

    public void setVlue(String vlue) {
        this.vlue = vlue;
    }

    public String getFileAttachmentId() {
        return fileAttachmentId;
    }

    public void setFileAttachmentId(String fileAttachmentId) {
        this.fileAttachmentId = fileAttachmentId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ListQrWalletDTO() {
    }

    public ListQrWalletDTO(String id, String description, String isPublic, String qrType, String timeCreate, String title, String content) {
        this.id = id;
        this.description = description;
        this.isPublic = isPublic;
        this.qrType = qrType;
        this.timeCreate = timeCreate;
        this.title = title;
        this.content = content;
    }
}
