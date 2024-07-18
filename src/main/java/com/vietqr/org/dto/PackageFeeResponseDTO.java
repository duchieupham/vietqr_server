package com.vietqr.org.dto;

public class PackageFeeResponseDTO {

    private int fixFee;
    private  double percentFee;

    public int getFixFee() {
        return fixFee;
    }

    public void setFixFee(int fixFee) {
        this.fixFee = fixFee;
    }

    public double getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(double percentFee) {
        this.percentFee = percentFee;
    }
}
