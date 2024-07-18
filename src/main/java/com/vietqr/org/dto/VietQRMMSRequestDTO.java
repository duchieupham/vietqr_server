package com.vietqr.org.dto;

import java.io.Serializable;

public class VietQRMMSRequestDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String token;
    private String terminalId;
    private String amount;
    private String content;
    private String orderId;

    public VietQRMMSRequestDTO() {
        super();
    }

    public VietQRMMSRequestDTO(String token, String terminalId, String amount, String content, String orderId) {
        this.token = token;
        this.terminalId = terminalId;
        this.amount = amount;
        this.content = content;
        this.orderId = orderId;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

}
