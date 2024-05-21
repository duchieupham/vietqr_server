package com.vietqr.org.service;

import com.vietqr.org.dto.*;
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

    List<IAdminInvoiceDTO> getInvoiceByMerchantId(String value, int offset, int size, String month);

    int countInvoiceByMerchantId(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(String value, int offset, int size, String month);

    int countInvoiceByInvoiceNumber(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByBankAccount(String value, int offset, int size, String month);

    int countInvoiceByBankAccount(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByPhoneNo(String value, int offset, int size, String month);

    int countInvoiceByPhoneNo(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByStatus(int status, int offset, int size, String month);

    int countInvoiceByStatus(int status, String month);

    List<IAdminInvoiceDTO> getInvoices(int offset, int size, String month);

    int countInvoice(String month);

    IAdminExtraInvoiceDTO getExtraInvoice(String month);

    IInvoiceQrDetailDTO getInvoiceQrById(String invoiceId);

    IInvoiceDTO getInvoiceByInvoiceDetail(String invoiceId);

    InvoiceUpdateItemDTO getInvoiceById(String invoiceId);

    void updateInvoiceById(long vatAmount, long totalAmount,
                           long totalAmountAfterVat, String invoiceId);

    void removeByInvoiceId(String invoiceId);

    InvoiceEntity getInvoiceEntityById(String invoiceId);
}
