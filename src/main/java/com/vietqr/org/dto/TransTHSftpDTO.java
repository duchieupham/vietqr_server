package com.vietqr.org.dto;

import java.io.Serializable;

public class TransTHSftpDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String no;
    private String date;
    private String service;
    private String matchedTotal;
    private String matchedAmountTotal;
    private String differenceTotal;
    private String differenceAmountTotal;
    private String result;
    private String addInfo;
    private String checkSum;

    public TransTHSftpDTO() {
    }

    public TransTHSftpDTO(String no, String date, String service, String matchedTotal, String matchedAmountTotal,
            String differenceTotal, String differenceAmountTotal, String result, String addInfo, String checkSum) {
        this.no = no;
        this.date = date;
        this.service = service;
        this.matchedTotal = matchedTotal;
        this.matchedAmountTotal = matchedAmountTotal;
        this.differenceTotal = differenceTotal;
        this.differenceAmountTotal = differenceAmountTotal;
        this.result = result;
        this.addInfo = addInfo;
        this.checkSum = checkSum;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMatchedTotal() {
        return matchedTotal;
    }

    public void setMatchedTotal(String matchedTotal) {
        this.matchedTotal = matchedTotal;
    }

    public String getMatchedAmountTotal() {
        return matchedAmountTotal;
    }

    public void setMatchedAmountTotal(String matchedAmountTotal) {
        this.matchedAmountTotal = matchedAmountTotal;
    }

    public String getDifferenceTotal() {
        return differenceTotal;
    }

    public void setDifferenceTotal(String differenceTotal) {
        this.differenceTotal = differenceTotal;
    }

    public String getDifferenceAmountTotal() {
        return differenceAmountTotal;
    }

    public void setDifferenceAmountTotal(String differenceAmountTotal) {
        this.differenceAmountTotal = differenceAmountTotal;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        return no + "|" + date + "|" + service + "|" + matchedTotal + "|" + matchedAmountTotal + "|" + differenceTotal
                + "|" + differenceAmountTotal + "|" + result + "|" + addInfo + "|" + checkSum;
    }

}
