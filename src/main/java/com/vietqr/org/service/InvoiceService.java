package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvoiceService {


    InvoiceEntity findInvoiceById(String invoiceId);

    void insert(InvoiceEntity entity);

    String getFileAttachmentId(String invoiceId);

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

    List<IInvoiceLatestDTO> getInvoiceLatestByUserId(String userId);

    List<IAdminInvoiceDTO> getInvoiceByMerchantName(String value, int offset, int size, String time);
    int countInvoiceByMerchantName(String value, String time);

    List<IAdminInvoiceDTO> getInvoiceByVsoCode(String value, int offset, int size, String time);

    int countInvoiceByVsoCode(String value, String time);

    IInvoicePaymentDTO getInvoicePaymentInfo(String invoiceId);

    List<IAdminInvoiceDTO> getAllInvoicesByInvoiceNumber(String value, int offset, int size);
    int countAllInvoicesByInvoiceNumber(String value);
    List<IAdminInvoiceDTO> getAllInvoicesByBankAccount(String value, int offset, int size);
    int countAllInvoicesByBankAccount(String value);
    List<IAdminInvoiceDTO> getAllInvoicesByPhoneNo(String value, int offset, int size);
    int countAllInvoicesByPhoneNo(String value);
    List<IAdminInvoiceDTO> getAllInvoicesByMerchantId(String value, int offset, int size);
    int countAllInvoicesByMerchantId(String value);
    List<IAdminInvoiceDTO> getAllInvoicesByMerchantName(String value, int offset, int size);
    int countAllInvoicesByMerchantName(String value);
    List<IAdminInvoiceDTO> getAllInvoicesByVsoCode(String value, int offset, int size);
    int countAllInvoicesByVsoCode(String value);
    List<IAdminInvoiceDTO> getAllInvoices(int offset, int size);
    int countAllInvoices();
    List<IAdminInvoiceDTO> getAllInvoicesByInvoiceNumber(int dataType, String value, int offset, int size);

    int countAllInvoicesByInvoiceNumber(int dataType, String value);

    List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(int dataType, String value,
                                                     int offset, int size, String time);

    int countInvoiceByInvoiceNumber(int dataType, String value, String time);

    List<IAdminInvoiceDTO> getAllInvoicesByBankAccount(int dataType, String value,
                                                       int offset, int size);

    int countAllInvoicesByBankAccount(int dataType, String value);

    List<IAdminInvoiceDTO> getInvoiceByBankAccount(int dataType, String value,
                                                   int offset, int size, String time);

    int countInvoiceByBankAccount(int dataType, String value, String time);

    List<IAdminInvoiceDTO> getAllInvoicesByPhoneNo(int dataType, String value, int offset, int size);

    int countAllInvoicesByPhoneNo(int dataType, String value);

    List<IAdminInvoiceDTO> getInvoiceByPhoneNo(int dataType, String value,
                                               int offset, int size, String time);

    int countInvoiceByPhoneNo(int dataType, String value, String time);

    List<IAdminInvoiceDTO> getAllInvoicesByMerchantId(int dataType, String value, int offset, int size);

    int countAllInvoicesByMerchantId(int dataType, String value);

    List<IAdminInvoiceDTO> getInvoiceByMerchantId(int dataType, String value,
                                                  int offset, int size, String time);

    int countInvoiceByMerchantId(int dataType, String value, String time);

    List<AdminMerchantDTO> getUnpaidInvoicesByMerchantId(String merchantId, int offset, int size);

    int countUnpaidInvoicesByMerchantId(String merchantId);


    List<IAdminInvoiceDTO> getAllInvoicesByStatus(String status, int offset, int size);

    int countAllInvoicesByStatus(String status);

    List<IAdminInvoiceDTO> getInvoicesByStatus(String status, int offset, int size, String time);

    int countInvoicesByStatus(String status, String time);
}
