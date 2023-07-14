package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankReceivePersonalDTO;
import com.vietqr.org.entity.BankReceivePersonalEntity;

@Repository
public interface AccountBankReceivePersonalRepository extends JpaRepository<BankReceivePersonalEntity, Long> {

    @Query(value = "SELECT a.bank_id as bankId, b.bank_account as bankAccount, b.bank_account_name as userBankName, c.bank_name as bankName, c.bank_code as bankCode, c.img_id as imgId, b.type as bankType, b.is_authenticated as authenticated, b.user_id as userId "
            + "FROM bank_receive_personal a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.user_id = :userId", nativeQuery = true)
    List<AccountBankReceivePersonalDTO> getPersonalBankReceive(@Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM bank_receive_personal WHERE bank_id = :bankId", nativeQuery = true)
    void deleteBankReceivePersonalByBankId(@Param(value = "bankId") String id);

}