package com.vietqr.org.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSettingConfigDTO {

    @JsonProperty(value = "bidvNotification")
    private boolean bidvNotification;

    public AccountSettingConfigDTO() {
        bidvNotification = true;
    }

    public AccountSettingConfigDTO(boolean bidvNotification) {
        this.bidvNotification = bidvNotification;
    }

    public boolean isBidvNotification() {
        return bidvNotification;
    }

    public void setBidvNotification(boolean bidvNotification) {
        this.bidvNotification = bidvNotification;
    }
}
