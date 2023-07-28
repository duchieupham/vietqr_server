package com.vietqr.org.dto;

public interface TransStatisticByDateDTO {
    String getDate();

    Long getTotalTrans();

    Long getTotalCashIn();

    Long getTotalCashOut();

    Long getTotalTransC();

    Long getTotalTransD();
}
