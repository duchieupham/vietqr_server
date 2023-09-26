package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.PaymentAnnualAccBankEntity;

@Repository
public interface PaymentAnnualAccBankRepository extends JpaRepository<PaymentAnnualAccBankEntity, Long> {

    @Query(value = "SELECT id FROM payment_annual_acc_bank WHERE bankId = :bankId AND from_date = :fromDate AND to_date = :toDate ", nativeQuery = true)
    List<String> checkExistedRecord(@Param(value = "bankId") String bankId,
            @Param(value = "fromDate") String fromDate,
            @Param(value = "toDate") String toDate);

}
