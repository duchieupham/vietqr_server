package com.vietqr.org.repository;

import com.vietqr.org.dto.DataTransactionDTO;
import com.vietqr.org.dto.FeeTransactionInfoDTO;
import com.vietqr.org.dto.TransReceiveInvoiceDTO;
import com.vietqr.org.dto.TransReceiveInvoicesDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomQueryRepository {
    List<TransReceiveInvoicesDTO> getTransReceiveInvoice(String tableName,
                                                         String bankId,
                                                         long fromDate,
                                                         long toDate);

    List<FeeTransactionInfoDTO> getTransactionInfoDataByBankId(String tableName,
                                                               String bankId,
                                                               long fromDate,
                                                               long toDate);

    List<DataTransactionDTO> findTransactionsByBankIdAndTimeRange(String tableName, String bankId, long fromDate, long toDate, int recordType);

    List<FeeTransactionInfoDTO> getTransactionInfoDataByBankIds(String tableName, List<String> bankIds, long fromDate, long toDate);

    List<FeeTransactionInfoDTO> getTransactionInfoDataByBankIdRecords(String tableName, List<String> bankIds, long fromDate, long toDate);
}
