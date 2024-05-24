package com.vietqr.org.repository;

import com.vietqr.org.dto.TransReceiveInvoiceDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomQueryRepository {
    List<TransReceiveInvoiceDTO> getTransReceiveInvoice(String tableName,
                                                        String bankId,
                                                        long fromDate,
                                                        long toDate);
}
