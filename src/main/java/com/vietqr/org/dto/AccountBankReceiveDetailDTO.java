package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class AccountBankReceiveDetailDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;
    private String bankAccount;
    private String userBankName;
    private String bankCode;
    private String bankName;
    private String imgId;
    private String bankTypeId;
    private int bankTypeStatus;
    private String caiValue;
    // userId who create bank account
    private String userId;
    private int type;
    private boolean isAuthenticated;
    private String nationalId;
    private String qrCode;
    private String phoneAuthenticated;
    private String ewalletToken;
    private Integer unlinkedType;
    private List<BusinessBankDetailDTO> businessDetails;
    private List<TransactionBankListDTO> transactions;

    public AccountBankReceiveDetailDTO() {
        super();
    }

    public AccountBankReceiveDetailDTO(String id, String bankAccount, String userBankName, String bankCode,
            String bankName, String imgId, String bankTypeId, int bankTypeStatus, String caiValue, String userId,
            int type,
            boolean isAuthenticated, String nationalId,
            String phoneAuthenticated, String qrCode,
            String ewalletToken, Integer unlinkedType,
            List<BusinessBankDetailDTO> businessDetails,
            List<TransactionBankListDTO> transactions) {
        this.id = id;
        this.bankAccount = bankAccount;
        this.userBankName = userBankName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.imgId = imgId;
        this.type = type;
        this.bankTypeId = bankTypeId;
        this.bankTypeStatus = bankTypeStatus;
        this.caiValue = caiValue;
        this.userId = userId;
        this.isAuthenticated = isAuthenticated;
        this.nationalId = nationalId;
        this.qrCode = qrCode;
        this.phoneAuthenticated = phoneAuthenticated;
        this.ewalletToken = ewalletToken;
        this.unlinkedType = unlinkedType;
        this.businessDetails = businessDetails;
        this.transactions = transactions;
    }

    public String getCaiValue() {
        return caiValue;
    }

    public void setCaiValue(String caiValue) {
        this.caiValue = caiValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getUserBankName() {
        return userBankName;
    }

    public void setUserBankName(String userBankName) {
        this.userBankName = userBankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBankTypeId() {
        return bankTypeId;
    }

    public void setBankTypeId(String bankTypeId) {
        this.bankTypeId = bankTypeId;
    }

    public int getBankTypeStatus() {
        return bankTypeStatus;
    }

    public void setBankTypeStatus(int bankTypeStatus) {
        this.bankTypeStatus = bankTypeStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneAuthenticated() {
        return phoneAuthenticated;
    }

    public void setPhoneAuthenticated(String phoneAuthenticated) {
        this.phoneAuthenticated = phoneAuthenticated;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getEwalletToken() {
        return ewalletToken;
    }

    public void setEwalletToken(String ewalletToken) {
        this.ewalletToken = ewalletToken;
    }

    public Integer getUnlinkedType() {
        return unlinkedType;
    }

    public void setUnlinkedType(Integer unlinkedType) {
        this.unlinkedType = unlinkedType;
    }

    public List<BusinessBankDetailDTO> getBusinessDetails() {
        return businessDetails;
    }

    public void setBusinessDetails(List<BusinessBankDetailDTO> businessDetails) {
        this.businessDetails = businessDetails;
    }

    public List<TransactionBankListDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionBankListDTO> transactions) {
        this.transactions = transactions;
    }

    public static class BusinessBankDetailDTO implements Serializable {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        private String businessId;
        private String businessName;
        private String imgId;
        private String coverImgId;
        private List<BranchBankDetailDTO> branchDetails;

        public BusinessBankDetailDTO() {
            super();
        }

        public BusinessBankDetailDTO(String businessId, String businessName, String imgId, String coverImgId,
                List<BranchBankDetailDTO> branchDetails) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.imgId = imgId;
            this.coverImgId = coverImgId;
            this.branchDetails = branchDetails;
        }

        public String getBusinessId() {
            return businessId;
        }

        public void setBusinessId(String businessId) {
            this.businessId = businessId;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getImgId() {
            return imgId;
        }

        public void setImgId(String imgId) {
            this.imgId = imgId;
        }

        public String getCoverImgId() {
            return coverImgId;
        }

        public void setCoverImgId(String coverImgId) {
            this.coverImgId = coverImgId;
        }

        public List<BranchBankDetailDTO> getBranchDetails() {
            return branchDetails;
        }

        public void setBranchDetails(List<BranchBankDetailDTO> branchDetails) {
            this.branchDetails = branchDetails;
        }

    }

    public static class BranchBankDetailDTO implements Serializable {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        private String branchId;
        private String branchName;
        private String code;
        private String address;

        public BranchBankDetailDTO() {
            super();
        }

        public BranchBankDetailDTO(String branchId, String branchName, String code, String address) {
            this.branchId = branchId;
            this.branchName = branchName;
            this.code = code;
            this.address = address;
        }

        public String getBranchId() {
            return branchId;
        }

        public void setBranchId(String branchId) {
            this.branchId = branchId;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

    }

    public static class TransactionBankListDTO implements Serializable {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        private String transactionId;
        private String bankAccount;
        private String bankId;
        private String amount;
        private String content;
        private int status;
        private long time;
        private int type;
        private String transType;

        public TransactionBankListDTO() {
            super();
        }

        public TransactionBankListDTO(String transactionId, String bankAccount, String bankId, String amount,
                String content, int status, long time, int type, String transType) {
            this.transactionId = transactionId;
            this.bankAccount = bankAccount;
            this.bankId = bankId;
            this.amount = amount;
            this.content = content;
            this.status = status;
            this.time = time;
            this.type = type;
            this.transType = transType;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getBankAccount() {
            return bankAccount;
        }

        public void setBankAccount(String bankAccount) {
            this.bankAccount = bankAccount;
        }

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTransType() {
            return transType;
        }

        public void setTransType(String transType) {
            this.transType = transType;
        }

    }
}
