package com.vietqr.org.repository;

import com.vietqr.org.dto.KeyActiveBankCheckDTO;
import com.vietqr.org.dto.KeyActiveBankReceiveDTO;
import com.vietqr.org.entity.KeyActiveBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface KeyActiveBankReceiveRepository extends JpaRepository<KeyActiveBankReceiveEntity, String> {
    @Query(value = "SELECT key_active AS keyActive, secret_key AS secretKey, "
            + "status AS status, duration AS duration, "
            + "value_active AS valueActive, create_at AS createAt, version AS version "
            + "FROM key_active_bank_receive "
            + "WHERE key_active = :keyActive LIMIT 1", nativeQuery = true)
    KeyActiveBankReceiveDTO checkKeyExist(@Param(value = "keyActive") String keyActive);

    @Query(value = "SELECT key_active AS keyActive, "
            + "status AS status, duration AS duration, COALESCE(secret_key, '') AS secretKey, "
            + "COALESCE(value_active, '') AS valueActive, create_at AS createAt "
            + "FROM key_active_bank_receive "
            + "WHERE key_active = :keyActive LIMIT 1", nativeQuery = true)
    KeyActiveBankCheckDTO checkKeyActiveByKey(@Param(value = "keyActive") String keyActive);

    @Transactional
    @Modifying
    @Query(value = "UPDATE key_active_bank_receive "
            + "SET status = 1, version = :newVersion "
            + "WHERE key_active = :keyActive AND version = :version", nativeQuery = true)
    int updateActiveKey(String keyActive, int version, int newVersion);

    @Query(value = "SELECT key_active AS keyActive "
            + "FROM key_active_bank_receive "
            + "WHERE key_active IN (:keyActives) ", nativeQuery = true)
    List<String> checkDuplicatedKeyActives(@Param(value = "keyActives") List<String> keyActives);

    @Query(value = "SELECT a.* FROM key_active_bank_receive a "
            + "WHERE bank_id = :bankId ", nativeQuery = true)
    List<KeyActiveBankReceiveEntity> getListKeyByBankId(String bankId);

    @Query(value = "SELECT a.bank_id FROM key_active_bank_receive a "
            + "WHERE key_active = :key ", nativeQuery = true)
    String getBankIdByKey(String key);

    @Query(value = "SELECT a.status AS statusActive FROM key_active_bank_receive a "
            + "WHERE key_active = :key ", nativeQuery = true)
    Integer getStatusByKeyAndBankId(String key);
}
