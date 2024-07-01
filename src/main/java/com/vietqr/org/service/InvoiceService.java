package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceService {


    InvoiceEntity findInvoiceById(String invoiceId);

    void insert(InvoiceEntity entity);

    List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, List<Integer> status, int offset, int size);

    String checkDuplicatedInvoiceId(String invoiceId);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, List<Integer> status, String month, int offset, int size);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, List<Integer> status, String bankId, int offset, int size);

    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status, String bankId,
                                                                  String month, int offset, int size);

    int countInvoiceByUserId(String userId, List<Integer> status);

    int countInvoiceByUserIdAndMonth(String userId, List<Integer> status, String time);

    int countInvoiceByUserIdAndBankId(String userId, List<Integer> status, String bankId);

    int countInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status, String bankId, String time);

    IInvoiceDetailDTO getInvoiceDetailById(String invoiceId);

    List<IAdminInvoiceDTO> getInvoiceByMerchantId(String value, int offset, int size, String month);

    List<IAdminInvoiceDTO> getInvoiceUnpaid(String value, int offset, int size, String month, String userId);

    int countInvoiceUnpaid(String value, String month, String userId);

    //List<String> getUserIdsByInvoiceId(String backAccount);

    int countInvoiceByMerchantId(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(String value, int offset, int size, String month);

    int countInvoiceByInvoiceNumber(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByBankAccount(String value, int offset, int size, String month);

    int countInvoiceByBankAccount(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByPhoneNo(String value, int offset, int size, String month);

    int countInvoiceByPhoneNo(String value, String month);

    List<IAdminInvoiceDTO> getInvoiceByStatus(int status, int offset, int size, String month);

    String getDataJson(String userId);

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

    InvoiceEntity getInvoiceEntityByRefId(String id, long amount);

    int updateStatusInvoice(String id, int status);

    int updateStatusInvoice(String id, int status, long timePaid);

    String checkExistedInvoice(String invoiceId);

    IInvoiceDTO getInvoiceRequestPayment(String invoiceId);

    InvoiceUnpaidStatisticDTO getTotalInvoiceUnpaidByUserId(String userId);

    String getBankIdRechargeDefault(String bankIdRecharge);

    List<UserScheduleInvoiceDTO> getUserScheduleInvoice();

    InvoiceEntity findInvoiceByInvoiceItemIds(String invoiceItemId);

    InvoiceUpdateVsoDTO getInvoicesByBankId(String bankId);

    void updateDataInvoiceByBankId(String data, String bankId);

    void updateFileInvoiceById(String id, String invoiceId);
}
