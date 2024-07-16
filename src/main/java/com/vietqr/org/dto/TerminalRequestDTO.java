package com.vietqr.org.dto;

import java.util.List;

public class TerminalRequestDTO {
    List<TerminalDTO> terminals;

    public List<TerminalDTO> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<TerminalDTO> terminals) {
        this.terminals = terminals;
    }

    public TerminalRequestDTO(List<TerminalDTO> terminals) {
        this.terminals = terminals;
    }

    public TerminalRequestDTO() {
    }

    public class TerminalDTO {
        private String terminalId;
        private String terminalName;
        private String terminalAddress;
        private String provinceCode;
        private String districtCode;
        private String wardsCode;
        private String mccCode;
        private int fee;
        private String bankCode;
        private String bankCodeBranch;
        private String bankAccountNumber;
        private String bankAccountName;
        private String bankCurrencyCode;

        public TerminalDTO() {
        }

        public TerminalDTO(String terminalId, String terminalName, String terminalAddress, String provinceCode, String districtCode, String wardsCode, String mccCode, int fee, String bankCode, String bankCodeBranch, String bankAccountNumber, String bankAccountName, String bankCurrencyCode) {
            this.terminalId = terminalId;
            this.terminalName = terminalName;
            this.terminalAddress = terminalAddress;
            this.provinceCode = provinceCode;
            this.districtCode = districtCode;
            this.wardsCode = wardsCode;
            this.mccCode = mccCode;
            this.fee = fee;
            this.bankCode = bankCode;
            this.bankCodeBranch = bankCodeBranch;
            this.bankAccountNumber = bankAccountNumber;
            this.bankAccountName = bankAccountName;
            this.bankCurrencyCode = bankCurrencyCode;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
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

        public String getDistrictCode() {
            return districtCode;
        }

        public void setDistrictCode(String districtCode) {
            this.districtCode = districtCode;
        }

        public String getWardsCode() {
            return wardsCode;
        }

        public void setWardsCode(String wardsCode) {
            this.wardsCode = wardsCode;
        }

        public String getMccCode() {
            return mccCode;
        }

        public void setMccCode(String mccCode) {
            this.mccCode = mccCode;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getBankCodeBranch() {
            return bankCodeBranch;
        }

        public void setBankCodeBranch(String bankCodeBranch) {
            this.bankCodeBranch = bankCodeBranch;
        }

        public String getBankAccountNumber() {
            return bankAccountNumber;
        }

        public void setBankAccountNumber(String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        public String getBankAccountName() {
            return bankAccountName;
        }

        public void setBankAccountName(String bankAccountName) {
            this.bankAccountName = bankAccountName;
        }

        public String getBankCurrencyCode() {
            return bankCurrencyCode;
        }

        public void setBankCurrencyCode(String bankCurrencyCode) {
            this.bankCurrencyCode = bankCurrencyCode;
        }
    }


}


