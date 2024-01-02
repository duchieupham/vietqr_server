package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class ServiceFeeDashboardDTO implements Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    private Long totalTrans;
    private Long totalAmount;
    private Long totalUnpaid;
    private Long totalPaid;
    private List<ServiceFeeMerchantItemDTO> list;

    public ServiceFeeDashboardDTO() {
        super();
    }

    public ServiceFeeDashboardDTO(Long totalTrans, Long totalAmount, Long totalUnpaid, Long totalPaid,
            List<ServiceFeeMerchantItemDTO> list) {
        this.totalTrans = totalTrans;
        this.totalAmount = totalAmount;
        this.totalUnpaid = totalUnpaid;
        this.totalPaid = totalPaid;
        this.list = list;
    }

    public Long getTotalTrans() {
        return totalTrans;
    }

    public void setTotalTrans(Long totalTrans) {
        this.totalTrans = totalTrans;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getTotalUnpaid() {
        return totalUnpaid;
    }

    public void setTotalUnpaid(Long totalUnpaid) {
        this.totalUnpaid = totalUnpaid;
    }

    public Long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public List<ServiceFeeMerchantItemDTO> getList() {
        return list;
    }

    public void setList(List<ServiceFeeMerchantItemDTO> list) {
        this.list = list;
    }

}
