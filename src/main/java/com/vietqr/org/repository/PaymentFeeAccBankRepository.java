package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.PaymentFeeAccBankEntity;

@Repository
public interface PaymentFeeAccBankRepository extends JpaRepository<PaymentFeeAccBankEntity, Long> {

    // @Query(value = "SELECT id FROM payment_fee_acc_bank WHERE bank_id = :bankId
    // AND ", nativeQuery = true)
    // List<String> checkExistedRecord();

}
