package com.vietqr.org.dto;

public interface TransactionQRDTO {
    // /**
    // *
    // */
    // private static final long serialVersionUID = 1L;

    // private String transactionId;
    // private String qr;
    // private Long amount;
    // private String content;
    // private String transType;
    // private String terminalCode;
    // private String orderId;
    // private String sign;
    // private int type;
    // private int status;
    // private Long timeCreated;
    // // bank account info
    // private String bankAccount;
    // private String bankCode;
    // private String bankName;
    // private String bankShortName;
    // private String imgId;

    String getTransactionId();

    String getBankId();

    // String getQrCode();

    Long getAmount();

    String getContent();

    String getTransType();

    String getTerminalCode();

    String getOrderId();

    // String getSign();

    Integer getType();

    Integer getStatus();

    Long getTimeCreated();

    String getBankAccount();

    String getBankTypeId();

    String getBankCode();

    String getBankName();

    String getBankShortName();

    String getImgId();

    String getUserBankName();

    String getQrCode();

    Boolean getMmsActive();

    String getNote();
}
