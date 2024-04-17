package com.vietqr.org.dto;

public interface AnnualFeeItemDTO {

    public String getAnnualBankId();

    public String getAccountBankFeeId();

    public String getserviceFeeId();

    public String getShortName();

    public Long getAnnualFee();

    public Integer getMonthlyCycle();

    public String getStartDate();

    public String getEndDate();

    public Double getVat();

    public Long getTotalPayment();

    public Integer getStatus();
}
