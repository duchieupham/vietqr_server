package com.vietqr.org.dto;

public class TransactionMMSDTO {
    // Số trace của hệ thống bank, do hệ thống bank tự sinh
    private String traceTransfer;

    // Tên Terminal, Tên cửa hàng
    private String storeLabel;

    // Terminal ID
    private String terminalLabel;

    // Số tiền trước khuyến mại nếu giao dịch đó được khuyến mại
    private String debitAmount;

    // - Nếu giao dịch không hưởng khuyến mại = debitAmount.
    // - Với những giao dịch được khuyến mại sẽ = debitAmount – Số tiền được hưởng
    // khuyến mại.
    private String realAmount;

    // Ngày giờ giao dịch
    // String theo format yyyyMMddHHmmss
    private String payDate;

    // mã lỗi
    private String respCode;

    // Mô tả mã lỗi
    private String respDesc;

    // Data mã hóa bằng Md5
    // checksum=traceTransfer+billNumber+payDate+debitAmount+acccessKey
    private String checkSum;

    // Tỷ giá ngoại
    // Ví dụ: 1 USD = 23.000 VND
    // Giá trị mặc định trống
    private String rate;

    // Mã hóa đơn merchant gửi cho MB khi tạo QR Code type = 1 (4 mới đúng chứ nhỉ)
    private String billNumber;

    // Có định dang MMYYYY + mã khách hàng, SĐT khách hàng hoặc số hóa đơn
    // Nếu muốn check giao dịch khi là QRcode type 2
    private String consumerLabelTerm;

    // Mã đơn hàng merchant gửi cho MB khi tạo QR Code type = 4
    private String referenceLabelCode;

    // Tên đầy đủ khách hàng
    private String userName;

    // Trả về mã FT nếu giao dịch thành công (phục vụ đối soát và hoàn tiền sau
    // này).
    // Đối tác cần check trùng theo trường thông tin này, nếu hệ thống MB gửi 2
    // lần thì giao dịch thứ 2 sẽ không ghi nhận.
    private String ftCode;

    // URL endpoint
    private String endPointUrl;

    public TransactionMMSDTO() {
        super();
    }

    public TransactionMMSDTO(String traceTransfer, String storeLabel, String terminalLabel, String debitAmount,
            String realAmount, String payDate, String respCode, String respDesc, String checkSum, String rate,
            String billNumber, String consumerLabelTerm, String referenceLabelCode, String userName, String ftCode,
            String endPointUrl) {
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
        return "TransactionMMSDTO [traceTransfer=" + traceTransfer + ", storeLabel=" + storeLabel + ", terminalLabel="
                + terminalLabel + ", debitAmount=" + debitAmount + ", realAmount=" + realAmount + ", payDate=" + payDate
                + ", respCode=" + respCode + ", respDesc=" + respDesc + ", checkSum=" + checkSum + ", rate=" + rate
                + ", billNumber=" + billNumber + ", consumerLabelTerm=" + consumerLabelTerm + ", referenceLabelCode="
                + referenceLabelCode + ", userName=" + userName + ", ftCode=" + ftCode + ", endPointUrl=" + endPointUrl
                + "]";
    }

}
