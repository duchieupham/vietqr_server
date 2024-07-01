package com.vietqr.org.repository;

import com.vietqr.org.entity.BankReceiveConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankReceiveConnectionRepository extends JpaRepository<BankReceiveConnectionEntity, String> {

    @Query(value = "SELECT * FROM bank_receive_connection WHERE bank_id = :bankId AND mid = :mid LIMIT 1",
            nativeQuery = true)
    BankReceiveConnectionEntity getBankReceiveConnectionByBankIdAndMid(String bankId, String mid);

    @Query(value = "SELECT * FROM bank_receive_connection "
            + "WHERE bank_id = :bankId AND is_active = TRUE ",
            nativeQuery = true)
    List<BankReceiveConnectionEntity> getBankReceiveConnectionByBankId(String bankId);

    @Query(value = "SELECT a.id FROM bank_receive_connection a "
            + "LEFT JOIN merchant_sync b ON a.mid = b.id "
            + "WHERE a.bank_id = :bankId "
            + "AND (a.mid != :mid OR b.ref_id != :mid) LIMIT 1 ", nativeQuery = true)
    String checkBankAccountByBankIdAndMid(String bankId, String mid);
}
