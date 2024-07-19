package com.vietqr.org.dto;

import java.util.List;

public class TerminalResponseFlow2 {
    private String clientMessageId;
    private Data data;
    private String errorCode;
    private List<String> errorDesc;

    public TerminalResponseFlow2(String clientMessageId, Data data, String errorCode, List<String> errorDesc) {
        this.clientMessageId = clientMessageId;
        this.data = data;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public TerminalResponseFlow2() {
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(List<String> errorDesc) {
        this.errorDesc = errorDesc;
    }

    public static class Data {
        private List<Terminal> terminals;
        private Pagination pagination;

        public Data(List<Terminal> terminals, Pagination pagination) {
            this.terminals = terminals;
            this.pagination = pagination;
        }

        public Data() {
        }

        public List<Terminal> getTerminals() {
            return terminals;
        }

        public void setTerminals(List<Terminal> terminals) {
            this.terminals = terminals;
        }

        public Pagination getPagination() {
            return pagination;
        }

        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }

        public static class Terminal {
            private String terminalId;
            private String merchantId;
            private String terminalName;
            private String terminalAddress;
            private String provinceCode;
            private String provinceName;
            private String districtCode;
            private String districtName;
            private String wardsCode;
            private String wardsName;
            private String mccCode;
            private String mccName;
            private int fee;
            private String bankAccountName;
            private String bankAccountNumber;
            private String bankCode;
            private String bankName;
            private String bankCurrencyCode;
            private String bankCurrencyName;
            private String bankCodeBranch;
            private String branchName;
            private int status;

            // Thêm trường thiếu
            private String sources;

            public Terminal() {
            }

            public Terminal(String terminalId, String merchantId, String terminalName, String terminalAddress, String provinceCode, String provinceName, String districtCode, String districtName, String wardsCode, String wardsName, String mccCode, String mccName, int fee, String bankAccountName, String bankAccountNumber, String bankCode, String bankName, String bankCurrencyCode, String bankCurrencyName, String bankCodeBranch, String branchName, int status) {
                this.terminalId = terminalId;
                this.merchantId = merchantId;
                this.terminalName = terminalName;
                this.terminalAddress = terminalAddress;
                this.provinceCode = provinceCode;
                this.provinceName = provinceName;
                this.districtCode = districtCode;
                this.districtName = districtName;
                this.wardsCode = wardsCode;
                this.wardsName = wardsName;
                this.mccCode = mccCode;
                this.mccName = mccName;
                this.fee = fee;
                this.bankAccountName = bankAccountName;
                this.bankAccountNumber = bankAccountNumber;
                this.bankCode = bankCode;
                this.bankName = bankName;
                this.bankCurrencyCode = bankCurrencyCode;
                this.bankCurrencyName = bankCurrencyName;
                this.bankCodeBranch = bankCodeBranch;
                this.branchName = branchName;
                this.status = status;
            }

            public String getSources() {
                return sources;
            }

            public void setSources(String sources) {
                this.sources = sources;
            }

            public String getTerminalId() {
                return terminalId;
            }

            public void setTerminalId(String terminalId) {
                this.terminalId = terminalId;
            }

            public String getMerchantId() {
                return merchantId;
            }

            public void setMerchantId(String merchantId) {
                this.merchantId = merchantId;
            }

            public String getTerminalName() {
                return terminalName;
            }

            public void setTerminalName(String terminalName) {
                this.terminalName = terminalName;
            }

            public String getTerminalAddress() {
                return terminalAddress;
            }

            public void setTerminalAddress(String terminalAddress) {
                this.terminalAddress = terminalAddress;
            }

            public String getProvinceCode() {
                return provinceCode;
            }

            public void setProvinceCode(String provinceCode) {
                this.provinceCode = provinceCode;
            }

            public String getProvinceName() {
                return provinceName;
            }

            public void setProvinceName(String provinceName) {
                this.provinceName = provinceName;
            }

            public String getDistrictCode() {
                return districtCode;
            }

            public void setDistrictCode(String districtCode) {
                this.districtCode = districtCode;
            }

            public String getDistrictName() {
                return districtName;
            }

            public void setDistrictName(String districtName) {
                this.districtName = districtName;
            }

            public String getWardsCode() {
                return wardsCode;
            }

            public void setWardsCode(String wardsCode) {
                this.wardsCode = wardsCode;
            }

            public String getWardsName() {
                return wardsName;
            }

            public void setWardsName(String wardsName) {
                this.wardsName = wardsName;
            }

            public String getMccCode() {
                return mccCode;
            }

            public void setMccCode(String mccCode) {
                this.mccCode = mccCode;
            }

            public String getMccName() {
                return mccName;
            }

            public void setMccName(String mccName) {
                this.mccName = mccName;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }

            public String getBankAccountName() {
                return bankAccountName;
            }

            public void setBankAccountName(String bankAccountName) {
                this.bankAccountName = bankAccountName;
            }

            public String getBankAccountNumber() {
                return bankAccountNumber;
            }

            public void setBankAccountNumber(String bankAccountNumber) {
                this.bankAccountNumber = bankAccountNumber;
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

            public String getBankCurrencyCode() {
                return bankCurrencyCode;
            }

            public void setBankCurrencyCode(String bankCurrencyCode) {
                this.bankCurrencyCode = bankCurrencyCode;
            }

            public String getBankCurrencyName() {
                return bankCurrencyName;
            }

            public void setBankCurrencyName(String bankCurrencyName) {
                this.bankCurrencyName = bankCurrencyName;
            }

            public String getBankCodeBranch() {
                return bankCodeBranch;
            }

            public void setBankCodeBranch(String bankCodeBranch) {
                this.bankCodeBranch = bankCodeBranch;
            }

            public String getBranchName() {
                return branchName;
            }

            public void setBranchName(String branchName) {
                this.branchName = branchName;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }
        }

        public static class Pagination {
            private int page;
            private int size;
            private int totalRecord;
            private int totalPage;

            public Pagination() {
            }

            public Pagination(int page, int size, int totalRecord, int totalPage) {
                this.page = page;
                this.size = size;
                this.totalRecord = totalRecord;
                this.totalPage = totalPage;
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getTotalRecord() {
                return totalRecord;
            }

            public void setTotalRecord(int totalRecord) {
                this.totalRecord = totalRecord;
            }

            public int getTotalPage() {
                return totalPage;
            }

            public void setTotalPage(int totalPage) {
                this.totalPage = totalPage;
            }
        }
    }
}
