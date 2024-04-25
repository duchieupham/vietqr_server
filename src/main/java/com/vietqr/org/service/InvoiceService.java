package com.vietqr.org.service;

import com.vietqr.org.dto.IInvoiceDetailDTO;
import com.vietqr.org.dto.IInvoiceResponseDTO;
import com.vietqr.org.entity.InvoiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceService {
    void insert(InvoiceEntity entity);

    List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, int status, int offset, int size);

    String checkDuplicatedInvoiceId(String invoiceId);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, int status, String month, int offset, int size);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, int status, String bankId, int offset, int size);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, int status, String bankId,
                                                                  String month, int offset, int size);

    int countInvoiceByUserId(String userId, int status);

    int countInvoiceByUserIdAndMonth(String userId, int status, String time);

    int countInvoiceByUserIdAndBankId(String userId, int status, String bankId);

    int countInvoiceByUserIdAndBankIdAndMonth(String userId, int status, String bankId, String time);

    IInvoiceDetailDTO getInvoiceDetailById(String invoiceId);
}
