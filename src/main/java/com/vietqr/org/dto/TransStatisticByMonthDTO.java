package com.vietqr.org.dto;

public interface TransStatisticByMonthDTO {
    String getMonth();

    Long getTotalTrans();

    Long getTotalCashIn();

    Long getTotalCashOut();

    Long getTotalTransC();

    Long getTotalTransD();
}
