package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionMMSEntity;

@Repository
public interface TransactionMMSRepository extends JpaRepository<TransactionMMSEntity, Long> {

    // check duplicate ftCode
    @Query(value = "SELECT ft_code FROM transactionmms WHERE ft_code = :ftCode", nativeQuery = true)
    String checkExistedFtCode(@Param(value = "ftCode") String ftCode);

    // @Query(value = "SELECT * FROM transactionmms WHERE ft_code = :ftCode",
    // nativeQuery = true)
    // TransactionMMSEntity getTransactionMMSByFtCode(@Param(value = "ftCode")
    // String ftCode);

    @Query(value = "SELECT * FROM transactionmms WHERE LEFT(pay_date, 8) = :date", nativeQuery = true)
    List<TransactionMMSEntity> getTransactionMMSByDate(@Param(value = "date") String date);

    @Query(value = "SELECT * FROM transactionmms WHERE ft_code = :ftCode AND pay_date LIKE %:payDate%", nativeQuery = true)
    TransactionMMSEntity getTransactionMMSByFtCode(@Param(value = "ftCode") String ftCode,
            @Param(value = "payDate") String payDate);

}
