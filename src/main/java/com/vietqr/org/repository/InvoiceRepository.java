package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, String> {
    @Query(value = "SELECT a.id AS invoiceId, '' AS billNumber, "
            + "'' AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, List<Integer> status, int offset, int size);

    @Query(value = "SELECT invoice_id FROM invoice "
            + "WHERE invoice_id = :invoiceId", nativeQuery = true)
    String checkDuplicatedInvoiceId(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, '' AS billNumber, "
            + "'' AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, List<Integer> status, long fromDate,
                                                         long toDate, int offset, int size);

    @Query(value = "SELECT a.id AS invoiceId, '' AS billNumber, "
            + "'' AS content, a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.bank_id = :bankId "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, List<Integer> status,
                                                          String bankId, int offset, int size);

    @Query(value = "SELECT a.id AS invoiceId, '' AS billNumber, "
            + "a.name AS invoiceName, a.time_created AS timeCreated, "
            + "a.status AS status, a.time_paid AS timePaid, a.bank_id AS bankId, "
            + "a.total_amount AS totalAmount, a.invoice_id AS invoiceNumber, "
            + "a.data AS data "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status, String bankId,
                                                                  long fromDate, long toDate, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) ", nativeQuery = true)
    int countInvoiceByUserId(String userId, List<Integer> status);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByUserIdAndMonth(String userId, List<Integer> status,
                                     long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.bank_id = :bankId ", nativeQuery = true)
    int countInvoiceByUserIdAndBankId(String userId, List<Integer> status, String bankId);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND a.status IN (:status) "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "AND a.bank_id = :bankId ", nativeQuery = true)
    int countInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status,
                                              String bankId, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, b.bill_number AS billNumber, "
            + "a.invoice_id AS invoiceNumber, a.name AS invoiceName, "
            + "a.time_created AS timeCreated, a.time_paid AS timePaid, "
            + "a.status AS status, a.vat_amount AS vatAmount, "
            + "a.vat AS vat, a.bank_id AS bankId, b.content AS content, "
            + "a.amount AS amount, a.total_amount AS totalAmount, "
            + "a.data AS data "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.id = :invoiceId ", nativeQuery = true)
    IInvoiceDetailDTO getInvoiceDetailById(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE b.id = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceByMerchantId(String value, int offset,
                                                  int size, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "LEFT JOIN account_bank_receive d ON a.bank_id = d.id "
            + "WHERE a.status = 0 and a.user_id = :userId "
            + " AND a.name LIKE %:value% AND a.time_created BETWEEN :fromDate AND :toDate "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceUnpaid(String value, int offset, int size, long fromDate, long toDate, String userId);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login b ON b.id = a.user_id "
            + "WHERE a.status = 0 AND a.user_id = :userId "
            + "AND a.name LIKE %:value% AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceUnpaid(String value, long fromDate, long toDate, String userId);


    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE b.id = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByMerchantId(String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.invoice_id = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(String value, int offset, int size,
                                                     long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.invoice_id = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByInvoiceNumber(String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE JSON_EXTRACT(a.data, '$.bankAccount') = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceByBankAccount(String value, int offset, int size,
                                                   long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE JSON_EXTRACT(a.data, '$.bankAccount') = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByBankAccount(String value, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE c.phone_no = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceByPhoneNo(String value, int offset, int size,
                                               long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE c.phone_no = :value "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByPhoneNo(String value, long fromDate, long toDate);

    @Query(value = "SELECT a.data FROM invoice a WHERE a.user_id :userId ", nativeQuery = true)
    String getDataJson(String userId);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoiceByStatus(int status, int offset, int size,
                                              long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.status = :status "
            + "AND a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoiceByStatus(int status, long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.time_paid AS timePaid, b.vso AS vso, b.name AS midName, "
            + "a.data AS data, a.amount AS amountNoVat, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.total_amount AS amount, a.invoice_id AS billNumber, a.name AS invoiceName, "
            + "c.phone_no AS phoneNo, c.email AS email, a.time_created AS timeCreated, a.status AS status "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.time_created BETWEEN :fromDate AND :toDate "
            + "ORDER BY a.time_created DESC "
            + "LIMIT :offset, :size", nativeQuery = true)
    List<IAdminInvoiceDTO> getInvoices(int offset, int size,
                                       long fromDate, long toDate);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM invoice a "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "LEFT JOIN merchant_sync b ON a.merchant_id = b.id "
            + "WHERE a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    int countInvoice(long fromDate, long toDate);

    @Query(value = "SELECT COALESCE(SUM(CASE WHEN a.status = 1 THEN a.total_amount ELSE 0 END), 0) AS completeFee, "
            + "COALESCE(COUNT(CASE WHEN a.status = 1 THEN a.id ELSE NULL END), 0) AS completeCount, "
            + "COALESCE(SUM(CASE WHEN a.status = 0 THEN a.total_amount ELSE 0 END), 0) AS pendingFee, "
            + "COALESCE(COUNT(CASE WHEN a.status != 1 THEN a.id ELSE NULL END), 0) AS pendingCount "
            + "FROM invoice a "
            + "WHERE a.time_created BETWEEN :fromDate AND :toDate ", nativeQuery = true)
    IAdminExtraInvoiceDTO getExtraInvoice(long fromDate, long toDate);

    @Query(value = "SELECT a.id AS invoiceId, a.name AS invoiceName, "
            + "a.invoice_id AS invoiceNumber, b.content AS content, "
            + "JSON_EXTRACT(a.data, '$.bankAccount') AS bankAccount, "
            + "JSON_EXTRACT(a.data, '$.userBankName') AS userBankName, "
            + "JSON_EXTRACT(a.data, '$.bankShortName') AS bankShortName, "
            + "a.amount AS totalAmount, a.vat AS vat, a.vat_amount AS vatAmount, "
            + "a.bank_id AS bankId, a.merchant_id AS merchantId, "
            + "a.total_amount AS totalAmountAfterVat, a.description AS description "
            + "FROM invoice a "
            + "INNER JOIN transaction_wallet b ON a.ref_id = b.id "
            + "WHERE a.id = :invoiceId LIMIT 1", nativeQuery = true)
    IInvoiceQrDetailDTO getInvoiceQrById(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, "
            + "a.name AS invoiceName, a.description AS invoiceDescription, "
            + "a.vat AS vat, a.vat_amount AS vatAmount, a.amount AS totalAMount, "
            + "a.total_amount AS totalAmountAfterVat, a.status AS status, a.merchant_id AS merchantId, "
            + "a.bank_id AS bankId, a.user_id AS userId, a.bank_id_recharge AS bankIdRecharge "
            + "FROM invoice a "
            + "WHERE a.id = :invoiceId ", nativeQuery = true)
    IInvoiceDTO getInvoiceByInvoiceDetail(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, a.amount AS totalAmount, a.ref_id AS refId, "
            + "a.vat_amount AS vatAmount, a.total_amount AS totalAmountAfterVat "
            + "FROM invoice a "
            + "WHERE a.id = :invoiceId ", nativeQuery = true)
    InvoiceUpdateItemDTO getInvoiceById(String invoiceId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE invoice SET vat_amount = :vatAmount, amount = :totalAmount, " +
            "total_amount = :totalAmountAfterVat WHERE id = :invoiceId ", nativeQuery = true)
    void updateInvoiceById(long vatAmount, long totalAmount,
                           long totalAmountAfterVat, String invoiceId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM invoice WHERE id = :invoiceId ", nativeQuery = true)
    void removeByInvoiceId(String invoiceId);

    @Query(value = "SELECT * FROM invoice WHERE id = :invoiceId ", nativeQuery = true)
    InvoiceEntity getInvoiceEntityById(String invoiceId);

    @Query(value = "SELECT * FROM invoice WHERE ref_id = :transWalletId AND total_amount = :amount LIMIT 1 ", nativeQuery = true)
    InvoiceEntity getInvoiceEntityByRefId(String transWalletId, long amount);

    @Transactional
    @Modifying
    @Query(value = "UPDATE invoice SET status = :status WHERE id = :id ", nativeQuery = true)
    int updateStatusInvoice(String id, int status);

    @Query(value = "SELECT id FROM invoice WHERE id = :invoiceId AND status != 1", nativeQuery = true)
    String checkExistedInvoice(String invoiceId);

    @Query(value = "SELECT a.id AS invoiceId, a.data AS data, "
            + "a.name AS invoiceName, a.vat AS vat, a.invoice_id AS invoiceNumber, "
            + "a.merchant_id AS merchantId, "
            + "a.bank_id AS bankId, a.user_id AS userId, a.bank_id_recharge AS bankIdRecharge "
            + "FROM invoice a "
            + "WHERE a.id = :invoiceId ", nativeQuery = true)
    IInvoiceDTO getInvoiceRequestPayment(String invoiceId);

    @Query(value = "SELECT COUNT(CASE WHEN a.status != 1 THEN a.id ELSE NULL END) AS totalInvoice, "
            + "COALESCE(SUM(a.total_amount), 0) AS totalMoney, "
            + "a.user_id AS userId "
            + "FROM invoice a "
            + "WHERE a.user_id = :userId AND (a.status = 0 OR a.status = 3) "
            + "GROUP BY a.user_id ", nativeQuery = true)
    InvoiceUnpaidStatisticDTO getTotalInvoiceUnpaidByUserId(String userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE invoice SET status = :status, time_paid = :timePaid WHERE id = :id ", nativeQuery = true)
    int updateStatusInvoice(String id, int status, long timePaid);

    @Query(value = "SELECT bank_id_recharge FROM invoice WHERE id = :id ", nativeQuery = true)
    String getBankIdRechargeDefault(String id);

    @Query(value = "SELECT COUNT(a.id) AS numberInvoice, a.user_id AS userId "
            + "FROM invoice a "
            + "WHERE (a.status = 0 OR a.status = 3) "
            + "GROUP BY a.user_id "
            + "HAVING numberInvoice > 0 ", nativeQuery = true)
    List<UserScheduleInvoiceDTO> getUserScheduleInvoice();
}
