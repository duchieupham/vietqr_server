package com.vietqr.org.repository;

import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import com.vietqr.org.entity.KeyBankAccountReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyBankAccountReceiveRepository extends JpaRepository<KeyBankAccountReceiveEntity, String> {
    @Query(value = "SELECT key AS key, "
            + "status AS status, duration AS duration, "
            + "value AS value, create_at AS createAt "
            + "FROM key_bank_account_receive "
            + "WHERE key = :key LIMIT 1", nativeQuery = true)
    KeyActiveBankReceiveDTO checkKeyExist(@Param(value = "key") String key);
}
