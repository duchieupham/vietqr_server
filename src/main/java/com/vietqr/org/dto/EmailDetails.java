package com.vietqr.org.dto;

public class EmailDetails {

    private String recipient;
    private String userId;
    private String attachment;

    public EmailDetails() {
    }

    public EmailDetails(String recipient, String userId, String attachment) {
        this.recipient = recipient;
        this.userId = userId;
        this.attachment = attachment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }


    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
