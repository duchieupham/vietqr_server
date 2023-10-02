package com.vietqr.org.dto;

import java.io.Serializable;

public class TotalPaymentDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long totalTrans;

    private Long totalPayment;

    private Long totalPaymentUnpaid;

    private Long totalPaymentPaid;

    public TotalPaymentDTO() {
        super();
    }

    public TotalPaymentDTO(Long totalTrans, Long totalPayment, Long totalPaymentUnpaid, Long totalPaymentPaid) {
        this.totalTrans = totalTrans;
        this.totalPayment = totalPayment;
        this.totalPaymentUnpaid = totalPaymentUnpaid;
        this.totalPaymentPaid = totalPaymentPaid;
    }

    public Long getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(Long totalTrans) {
        this.totalTrans = totalTrans;
    }

    public Long getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Long totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Long getTotalPaymentUnpaid() {
        return totalPaymentUnpaid;
    }

    public void setTotalPaymentUnpaid(Long totalPaymentUnpaid) {
        this.totalPaymentUnpaid = totalPaymentUnpaid;
    }

    public Long getTotalPaymentPaid() {
        return totalPaymentPaid;
    }

    public void setTotalPaymentPaid(Long totalPaymentPaid) {
        this.totalPaymentPaid = totalPaymentPaid;
    }

}
