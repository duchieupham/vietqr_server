package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.PaymentFeeAccBankEntity;

@Repository
public interface PaymentFeeAccBankRepository extends JpaRepository<PaymentFeeAccBankEntity, Long> {

    @Query(value = "SELECT * FROM payment_fee_acc_bank WHERE bank_id = :bankId AND "
            + "account_bank_fee_id = :accBankFeeId AND month = :month ", nativeQuery = true)
    PaymentFeeAccBankEntity checkExistedRecord(
            @Param(value = "bankId") String bankId,
            @Param(value = "accBankFeeId") String accBankFeeId,
            @Param(value = "month") String month);

}
