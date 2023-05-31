package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TerminalBank")
public class TerminalBankEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "terminalId")
    private String terminalId;

    @Column(name = "merchantId")
    private String merchantId;

    @Column(name = "terminalName")
    private String terminalName;

    @Column(name = "terminalAddress")
    private String terminalAddress;

    @Column(name = "provinceCode")
    private String provinceCode;

    @Column(name = "provinceName")
    private String provinceName;

    @Column(name = "districtCode")
    private String districtCode;

    @Column(name = "districtName")
    private String districtName;

    @Column(name = "wardsCode")
    private String wardsCode;

    @Column(name = "wardsName")
    private String wardsName;

    @Column(name = "mccCode")
    private String mccCode;

    @Column(name = "mccName")
    private String mccName;

    @Column(name = "fee")
    private double fee;

    @Column(name = "bankAccountName")
    private String bankAccountName;

    @Column(name = "bankAccountNumber")
    private String bankAccountNumber;

    @Column(name = "bankAccountRawNumber")
    private String bankAccountRawNumber;

    @Column(name = "bankCode")
    private String bankCode;

    @Column(name = "bankName")
    private String bankName;

    @Column(name = "bankCurrencyCode")
    private String bankCurrencyCode;

    @Column(name = "bankCurrencyName")
    private String bankCurrencyName;

    @Column(name = "branchName")
    private String branchName;

    @Column(name = "status")
    private int status;

    public TerminalBankEntity() {
        super();
    }

    public TerminalBankEntity(String id, String terminalId, String merchantId, String terminalName,
            String terminalAddress, String provinceCode, String provinceName, String districtCode, String districtName,
            String wardsCode, String wardsName, String mccCode, String mccName, double fee, String bankAccountName,
            String bankAccountNumber, String bankAccountRawNumber, String bankCode, String bankName,
            String bankCurrencyCode,
            String bankCurrencyName, String branchName, int status) {
        this.id = id;
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
        this.bankAccountRawNumber = bankAccountRawNumber;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.bankCurrencyCode = bankCurrencyCode;
        this.bankCurrencyName = bankCurrencyName;
        this.branchName = branchName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
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

    @Override
    public String toString() {
        return "TerminalBankEntity [id=" + id + ", terminalId=" + terminalId + ", merchantId=" + merchantId
                + ", terminalName=" + terminalName + ", terminalAddress=" + terminalAddress + ", provinceCode="
                + provinceCode + ", provinceName=" + provinceName + ", districtCode=" + districtCode + ", districtName="
                + districtName + ", wardsCode=" + wardsCode + ", wardsName=" + wardsName + ", mccCode=" + mccCode
                + ", mccName=" + mccName + ", fee=" + fee + ", bankAccountName=" + bankAccountName
                + ", bankAccountNumber=" + bankAccountNumber + ", bankCode=" + bankCode + ", bankName=" + bankName
                + ", bankCurrencyCode=" + bankCurrencyCode + ", bankCurrencyName=" + bankCurrencyName + ", branchName="
                + branchName + ", status=" + status + "]";
    }

}
