package com.vietqr.org.repository;

import com.vietqr.org.dto.IBankAccountInvoiceDTO;
import com.vietqr.org.dto.IBankDetailAdminDTO;
import com.vietqr.org.dto.IFeePackageDetailDTO;
import com.vietqr.org.dto.IInvoiceItemCreateDTO;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankReceiveFeePackageRepository extends JpaRepository<BankReceiveFeePackageEntity, String> {
    @Query(value = "SELECT a.bank_id AS bankId, a.data AS data, d.title AS feePackage, "
            + "c.phone_no AS phoneNo, c.email AS email "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "INNER JOIN bank_receive_fee_package d ON d.bank_id = a.bank_id "
            + "WHERE b.bank_account = :value "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "INNER JOIN bank_receive_fee_package d ON d.bank_id = a.bank_id "
            + "WHERE b.bank_account = :value ", nativeQuery = true)
    int countBankInvoiceByBankAccount(String value);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "INNER JOIN bank_receive_fee_package d ON d.bank_id = a.bank_id "
            + "INNER JOIN merchant_connection e ON e.id = a.mid_connect_id "
            + "WHERE b.bank_account = :value AND e.mid = :merchantId ", nativeQuery = true)
    int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value);

    @Query(value = "SELECT a.title AS feePackage, "
            + "a.annual_fee AS annualFee, a.fix_fee AS fixFee, a.vat AS vat, "
            + "a.percent_fee AS percentFee, a.record_type AS recordType "
            + "FROM bank_receive_fee_package a "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    List<IFeePackageDetailDTO> getFeePackageDetail(String bankId);

    @Query(value = "SELECT a.annual_fee AS annualFee, a.fix_fee AS fixFee, "
            + "a.percent_fee AS percentFee, a.record_type AS recordType, "
            + "a.active_fee AS activeFee, a.vat AS vat "
            + "FROM bank_receive_fee_package a "
            + "WHERE a.bank_id = :bankId LIMIT 1", nativeQuery = true)
    IInvoiceItemCreateDTO getFeePackageByBankId(String bankId);

    @Query(value = "SELECT a.bank_id AS bankId, a.data AS data, d.title AS feePackage, "
            + "c.phone_no AS phoneNo, c.email AS email "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "INNER JOIN bank_receive_fee_package d ON d.bank_id = a.bank_id "
            + "INNER JOIN merchant_connection e ON e.id = a.mid_connect_id "
            + "WHERE b.bank_account = :value AND e.mid = :merchantId "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
                                                                          int offset, int size);
    @Query(value = "SELECT a.bank_id AS bankId, '' AS merchantId, "
            + "b.bank_account_name AS userBankName, b.bank_account AS bankAccount, "
            + "c.bank_short_name AS bankShortName, d.phone_no AS phoneNo, "
            + "d.email AS email, b.mms_active AS mmsActive, "
            + "a.title AS feePackage, a.vat AS vat, "
            + "a.fix_fee AS transFee1, a.percent_fee AS transFee2, "
            + "a.record_type AS transRecord "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN bank_type c ON c.id = b.bank_type_id "
            + "INNER JOIN account_login d ON d.id = b.user_id "
            + "WHERE a.bank_id = :bankId", nativeQuery = true)
    IBankDetailAdminDTO getBankReceiveByBankId(String bankId);
}
