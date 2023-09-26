package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.AccountBankFeeEntity;

@Repository
public interface AccountbankFeeRepository extends JpaRepository<AccountBankFeeEntity, Long> {

    @Query(value = "SELECT * FROM account_bank_fee WHERE bank_id = :bankId ", nativeQuery = true)
    List<AccountBankFeeEntity> getAccountBankFeesByBankId(@Param(value = "bankId") String bankId);

    // update start Date - end Date
    @Transactional
    @Modifying
    @Query(value = "UPDATE account_bank_fee SET start_date = :startDate WHERE id = :id", nativeQuery = true)
    void updateStartDate(@Param(value = "startDate") String startDate, @Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_bank_fee SET end_date = :endDate WHERE id = :id", nativeQuery = true)
    void updateEndDate(@Param(value = "endDate") String endDate, @Param(value = "id") String id);

}
