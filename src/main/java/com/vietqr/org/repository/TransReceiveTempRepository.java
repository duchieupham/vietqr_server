package com.vietqr.org.repository;

import com.vietqr.org.entity.TransReceiveTempEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransReceiveTempRepository extends JpaRepository<TransReceiveTempEntity, String> {
    @Query(value = "SELECT * FROM trans_receive_temp "
            + "WHERE bank_id = :bankId LIMIT 1", nativeQuery = true)
    TransReceiveTempEntity getLastTimeByBankId(String bankId);
}
