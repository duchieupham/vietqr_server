package com.vietqr.org.repository;

import com.vietqr.org.dto.IBankAccountInvoiceDTO;
import com.vietqr.org.dto.IFeePackageDetailDTO;
import com.vietqr.org.entity.BankReceiveFeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankReceiveFeePackageRepository extends JpaRepository<BankReceiveFeePackageEntity, String> {
//    @Query(value = "SELECT "
//            + "FROM bank_receive_connection ", nativeQuery = true)
//    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccount(String value, int offset, int size);
//
//    int countBankInvoiceByBankAccount(String value);
//
//    List<IBankAccountInvoiceDTO> getBankInvoiceByBankAccountAndMerchantId(String merchantId, String value,
//                                                                          int offset, int size);
//
//    int countBankInvoiceByBankAccountAndMerchantId(String merchantId, String value);

    @Query(value = "SELECT a.title AS feePackage, "
            + "a.annual_fee AS annualFee, a.fix_fee AS fixFee, "
            + "a.percent_fee AS percentFee, a.record_type AS recordType "
            + "FROM bank_receive_fee_package a "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    List<IFeePackageDetailDTO> getFeePackageDetail(String bankId);
}
