package com.vietqr.org.dto.mapping;

public class RefundMappingRedisDTO {
    private String terminalCode;
    private String subTerminalCode;
    private String refNumber;
    private String orderId;

    public RefundMappingRedisDTO() {
    }

    public RefundMappingRedisDTO(String terminalCode, String subTerminalCode, String refNumber, String orderId) {
        this.terminalCode = terminalCode;
        this.subTerminalCode = subTerminalCode;
        this.refNumber = refNumber;
        this.orderId = orderId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getSubTerminalCode() {
        return subTerminalCode;
    }

    public void setSubTerminalCode(String subTerminalCode) {
        this.subTerminalCode = subTerminalCode;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
