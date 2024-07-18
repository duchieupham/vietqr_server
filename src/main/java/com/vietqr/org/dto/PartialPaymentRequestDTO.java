package com.vietqr.org.dto;

import java.util.List;

public class PartialPaymentRequestDTO {
    private List<String> itemIds; // Danh sách ID của các item muốn thanh toán
    private String bankId; // ID của ngân hàng thực hiện thanh toán
    private String userId; // ID của người dùng thực hiện thanh toán

    public PartialPaymentRequestDTO() {
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
