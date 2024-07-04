package com.vietqr.org.repository;

import com.vietqr.org.dto.TransTempCountDTO;
import com.vietqr.org.entity.TransReceiveTempEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransReceiveTempRepository extends JpaRepository<TransReceiveTempEntity, String> {
    @Query(value = "SELECT * FROM trans_receive_temp "
            + "WHERE bank_id = :bankId LIMIT 1", nativeQuery = true)
    TransReceiveTempEntity getLastTimeByBankId(String bankId);

    @Query(value = "SELECT bank_id AS bankId, "
            + "MAX(last_times) AS lastTimes, MAX(nums) AS nums "
            + "FROM trans_receive_temp "
            + "WHERE bank_id IN (:bankIds) "
            + "GROUP BY bankId", nativeQuery = true)
    List<TransTempCountDTO> getTransTempCounts(List<String> bankIds);

    @Query(value = "SELECT bank_id AS bankId, "
            + "MAX(last_times) AS lastTimes, MAX(nums) AS nums "
            + "FROM trans_receive_temp "
            + "WHERE bank_id = :bankId GROUP BY bankId LIMIT 1", nativeQuery = true)
    TransTempCountDTO getTransTempCount(String bankId);
}
