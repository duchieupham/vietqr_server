package com.vietqr.org.repository;

import com.vietqr.org.dto.*;
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
            + "WHERE b.bank_account LIKE %:value% "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "INNER JOIN bank_receive_fee_package d ON d.bank_id = a.bank_id "
            + "WHERE b.bank_account LIKE %:value% ", nativeQuery = true)
    int countBankInvoiceByBankAccount(String value);

    @Query(value = "SELECT COUNT(a.bank_id) "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN account_login b ON b.id = a.user_id "
            + "WHERE JSON_EXTRACT(a.data, '$.bankAccount') LIKE %:value% "
            + "AND a.mid = :merchantId ", nativeQuery = true)
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

    @Query(value = "SELECT a.bank_id AS bankId, a.data AS data, a.title AS feePackage, "
            + "b.phone_no AS phoneNo, b.email AS email "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN account_login b ON b.id = a.user_id "
            + "WHERE JSON_EXTRACT(a.data, '$.bankAccount') LIKE %:value% "
            + "AND a.mid = :merchantId "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
                                                                          int offset, int size);

    @Query(value = "SELECT a.bank_id AS bankId, a.mid AS merchantId, "
            + "a.data AS data, b.phone_no AS phoneNo, "
            + "b.email AS email, "
            + "a.title AS feePackage, a.vat AS vat, "
            + "a.fix_fee AS transFee1, a.percent_fee AS transFee2, "
            + "a.record_type AS transRecord "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN account_login b ON b.id = a.user_id "
            + "WHERE a.bank_id = :bankId", nativeQuery = true)
    IBankDetailAdminDTO getBankReceiveByBankId(String bankId);

    @Query(value = "SELECT a.title AS feePackage, "
            + "b.email AS email, b.phone_no AS phoneNo, a.user_id AS userId, "
            + "a.data AS data, a.bank_id AS bankId, a.mid AS merchantId "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN account_login b ON b.id = a.user_id "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    IBankAccountInvoiceDTO getBankInvoiceByBankId(String bankId);

    @Query(value = "SELECT b.vso AS vso, b.name AS merchantName, "
            + "c.email AS email, c.phone_no AS phoneNo, a.user_id AS userId, "
            + "a.data AS data "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN merchant_sync b ON b.id = a.mid "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "WHERE a.bank_id = :bankId AND a.mid = :merchantId ", nativeQuery = true)
    IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId);

    @Query(value = "SELECT b.vso AS vso, b.name AS merchantName, "
            + "c.email AS email, c.phone_no AS phoneNo, '' AS platform, "
            + "JSON_EXTRACT(a.data, '$.bankAccount') AS bankAccount, "
            + "JSON_EXTRACT(a.data, '$.bankShortName') AS bankShortName, "
            + "JSON_EXTRACT(a.data, '$.userBankName') AS userBankName, "
            + "JSON_EXTRACT(a.data, '$.mmsActive') AS mmsActive "
            + "FROM bank_receive_fee_package a "
            + "INNER JOIN merchant_sync b ON b.id = a.mid "
            + "INNER JOIN account_login c ON c.id = a.user_id "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    List<ICustomerDetailDTO> getCustomerDetailByBankId(String bankId);

    @Query(value="SELECT a.vat AS VAT , a.record_type AS recordType, "
            + "COALESCE(JSON_UNQUOTE(JSON_EXTRACT(a.data, '$.bankAccount')), '') AS bankAccount,"
            + "COALESCE(JSON_UNQUOTE(JSON_EXTRACT(a.data, '$.bankShortName')), '') AS bankShortName, "
            + "COALESCE(JSON_UNQUOTE(JSON_EXTRACT(a.data, '$.userBankName')), '') AS userBankName, "
            + "COALESCE(JSON_UNQUOTE(JSON_EXTRACT(a.data, '$.mmsActive')), '') as mmsActive, "
            + "a.title AS title , a.fix_fee AS fixFee, a.percent_fee AS percentFee "
            + "FROM bank_receive_fee_package a "
            + "WHERE a.bank_id = :bankId", nativeQuery = true)
    IBankReceiveFeePackageDTO getCustomerInfoById( String bankId);
}
