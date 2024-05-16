package com.vietqr.org.repository;

import com.vietqr.org.dto.IBankAccountInvoiceDTO;
import com.vietqr.org.dto.IFeePackageDetailDTO;
import com.vietqr.org.dto.IInvoiceItemCreateDTO;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankReceiveFeePackageRepository extends JpaRepository<BankReceiveFeePackageEntity, String> {
    @Query(value = "SELECT a.bank_id AS bankId, a.data AS data, a.title AS feePackage, "
            + "c.phone_no AS phoneNo, a.email AS email "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "WHERE b.bank_account = :value "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN account_login c ON c.id = b.user_id "
            + "WHERE b.bank_account = :value ", nativeQuery = true)
    int countBankInvoiceByBankAccount(String value);
//
//    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
//                                                                          int offset, int size);
//
//    int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value);

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
}
