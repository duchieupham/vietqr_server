package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AnnualFeeItemDTO;
import com.vietqr.org.entity.PaymentAnnualAccBankEntity;

@Repository
public interface PaymentAnnualAccBankRepository extends JpaRepository<PaymentAnnualAccBankEntity, Long> {

    @Query(value = "SELECT a.id as annualBankId, b.id as accountBankFeeId, b.service_fee_id as serviceFeeId, "
            + "b.short_name as shortName, b.annual_fee as annualFee, b.monthly_cycle as monthlyCycle,  "
            + "a.from_date as startDate, a.to_date as endDate, b.vat, a.total_payment as totalPayment,  "
            + "a.status  "
            + "FROM payment_annual_acc_bank a  "
            + "INNER JOIN account_bank_fee b  "
            + "ON a.account_bank_fee_id = b.id  "
            + "WHERE a.bank_id = :bankId ", nativeQuery = true)
    List<AnnualFeeItemDTO> getAnnualFeesByBankId(@Param(value = "bankId") String bankId);
}
