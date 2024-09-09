package com.vietqr.org.dto;

import java.util.List;

public class GoogleSheetDetailDTO {
    private String id;
    private String name;
    private String webhook;
    private String userId;
    private List<GoogleSheetBankDTO> banks;
    private List<String> notificationTypes;
    private List<String> notificationContents;

    public GoogleSheetDetailDTO() {
        super();
    }

    public GoogleSheetDetailDTO(String id, String name, String webhook, String userId, List<GoogleSheetBankDTO> banks, List<String> notificationTypes, List<String> notificationContents) {
        this.id = id;
        this.name = name;
        this.webhook = webhook;
        this.userId = userId;
        this.banks = banks;
        this.notificationTypes = notificationTypes;
        this.notificationContents = notificationContents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<GoogleSheetBankDTO> getBanks() {
        return banks;
    }

    public void setBanks(List<GoogleSheetBankDTO> banks) {
        this.banks = banks;
    }

    public List<String> getNotificationTypes() {
        return notificationTypes;
    }

    public void setNotificationTypes(List<String> notificationTypes) {
        this.notificationTypes = notificationTypes;
    }

    public List<String> getNotificationContents() {
        return notificationContents;
    }

    public void setNotificationContents(List<String> notificationContents) {
        this.notificationContents = notificationContents;
    }
}
