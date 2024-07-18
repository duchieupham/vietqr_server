package com.vietqr.org.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TransactionMMS")
public class TransactionMMSEntity implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    // Số trace của hệ thống bank, do hệ thống bank tự sinh
    @Column(name = "traceTransfer")
    private String traceTransfer;

    // Tên Terminal, Tên cửa hàng
    @Column(name = "storeLabel")
    private String storeLabel;

    // Terminal ID
    @Column(name = "terminalLabel")
    private String terminalLabel;

    // Số tiền trước khuyến mại nếu giao dịch đó được khuyến mại
    @Column(name = "debitAmount")
    private String debitAmount;

    // - Nếu giao dịch không hưởng khuyến mại = debitAmount.
    // - Với những giao dịch được khuyến mại sẽ = debitAmount – Số tiền được hưởng
    // khuyến mại.
    @Column(name = "realAmount")
    private String realAmount;

    // Ngày giờ giao dịch
    // String theo format yyyyMMddHHmmss
    @Column(name = "payDate")
    private String payDate;

    // mã lỗi
    @Column(name = "respCode")
    private String respCode;

    // Mô tả mã lỗi
    @Column(name = "respDesc")
    private String respDesc;

    // Data mã hóa bằng Md5
    // checksum=traceTransfer+billNumber+payDate+debitAmount+acccessKey
    @Column(name = "checkSum")
    private String checkSum;

    // Tỷ giá ngoại
    // Ví dụ: 1 USD = 23.000 VND
    // Giá trị mặc định trống
    @Column(name = "rate")
    private String rate;

    // Mã hóa đơn merchant gửi cho MB khi tạo QR Code type = 1 (4 mới đúng chứ nhỉ)
    @Column(name = "billNumber")
    private String billNumber;

    // Có định dang MMYYYY + mã khách hàng, SĐT khách hàng hoặc số hóa đơn
    // Nếu muốn check giao dịch khi là QRcode type 2
    @Column(name = "consumerLabelTerm")
    private String consumerLabelTerm;

    // Mã đơn hàng merchant gửi cho MB khi tạo QR Code type = 4
    @Column(name = "referenceLabelCode")
    private String referenceLabelCode;

    // Tên đầy đủ khách hàng
    @Column(name = "userName")
    private String userName;

    // Trả về mã FT nếu giao dịch thành công (phục vụ đối soát và hoàn tiền sau
    // này).
    // Đối tác cần check trùng theo trường thông tin này, nếu hệ thống MB gửi 2
    // lần thì giao dịch thứ 2 sẽ không ghi nhận.
    @Column(name = "ftCode")
    private String ftCode;

    // URL endpoint
    @Column(name = "endPointUrl")
    private String endPointUrl;

    public TransactionMMSEntity() {
        super();
    }

    public TransactionMMSEntity(String id, String traceTransfer, String storeLabel, String terminalLabel,
            String debitAmount, String realAmount, String payDate, String respCode, String respDesc, String checkSum,
            String rate, String billNumber, String consumerLabelTerm, String referenceLabelCode, String userName,
            String ftCode, String endPointUrl) {
        this.id = id;
        this.traceTransfer = traceTransfer;
        this.storeLabel = storeLabel;
        this.terminalLabel = terminalLabel;
        this.debitAmount = debitAmount;
        this.realAmount = realAmount;
        this.payDate = payDate;
        this.respCode = respCode;
        this.respDesc = respDesc;
        this.checkSum = checkSum;
        this.rate = rate;
        this.billNumber = billNumber;
        this.consumerLabelTerm = consumerLabelTerm;
        this.referenceLabelCode = referenceLabelCode;
        this.userName = userName;
        this.ftCode = ftCode;
        this.endPointUrl = endPointUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTraceTransfer() {
        return traceTransfer;
    }

    public void setTraceTransfer(String traceTransfer) {
        this.traceTransfer = traceTransfer;
    }

    public String getStoreLabel() {
        return storeLabel;
    }

    public void setStoreLabel(String storeLabel) {
        this.storeLabel = storeLabel;
    }

    public String getTerminalLabel() {
        return terminalLabel;
    }

    public void setTerminalLabel(String terminalLabel) {
        this.terminalLabel = terminalLabel;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(String realAmount) {
        this.realAmount = realAmount;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDesc() {
        return respDesc;
    }

    public void setRespDesc(String respDesc) {
        this.respDesc = respDesc;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getConsumerLabelTerm() {
        return consumerLabelTerm;
    }

    public void setConsumerLabelTerm(String consumerLabelTerm) {
        this.consumerLabelTerm = consumerLabelTerm;
    }

    public String getReferenceLabelCode() {
        return referenceLabelCode;
    }

    public void setReferenceLabelCode(String referenceLabelCode) {
        this.referenceLabelCode = referenceLabelCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFtCode() {
        return ftCode;
    }

    public void setFtCode(String ftCode) {
        this.ftCode = ftCode;
    }

    public String getEndPointUrl() {
        return endPointUrl;
    }

    public void setEndPointUrl(String endPointUrl) {
        this.endPointUrl = endPointUrl;
    }

    @Override
    public String toString() {
        return "TransactionMMSEntity [id=" + id + ", traceTransfer=" + traceTransfer + ", storeLabel=" + storeLabel
                + ", terminalLabel=" + terminalLabel + ", debitAmount=" + debitAmount + ", realAmount=" + realAmount
                + ", payDate=" + payDate + ", respCode=" + respCode + ", respDesc=" + respDesc + ", checkSum="
                + checkSum + ", rate=" + rate + ", billNumber=" + billNumber + ", consumerLabelTerm="
                + consumerLabelTerm + ", referenceLabelCode=" + referenceLabelCode + ", userName=" + userName
                + ", ftCode=" + ftCode + ", endPointUrl=" + endPointUrl + "]";
    }

}
