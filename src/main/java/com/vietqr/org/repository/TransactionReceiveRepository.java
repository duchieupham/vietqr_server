package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.vietqr.org.entity.TransactionReceiveEntity;
import com.vietqr.org.dto.TransactionRelatedDTO;

@Repository
public interface TransactionReceiveRepository extends JpaRepository<TransactionReceiveEntity, Long> {

        @Query(value = "SELECT * FROM transaction_receive WHERE id = :id", nativeQuery = true)
        TransactionReceiveEntity getTransactionReceiveById(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_receive SET status = :status, ref_id = :refId WHERE id = :id", nativeQuery = true)
        void updateTransactionReceiveStatus(@Param(value = "status") int status, @Param(value = "refId") String refId,
                        @Param(value = "id") String id);

        @Query(value = "SELECT b.id as transactionId, b.amount, b.bank_account as bankAccount, b.content, b.time, b.status "
                        + "FROM transaction_receive_branch a "
                        + "INNER JOIN transaction_receive b "
                        + "ON a.transaction_receive_id = b.id "
                        + "WHERE a.business_id = :businessId "
                        + "ORDER BY b.time DESC LIMIT 5", nativeQuery = true)
        List<TransactionRelatedDTO> getRelatedTransactionReceives(@Param(value = "businessId") String businessId);

        @Query(value = "SELECT * "
                        + "FROM transaction_receive "
                        + "WHERE id = :id "
                        + "AND status = 0", nativeQuery = true)
        TransactionReceiveEntity getTransactionById(@Param(value = "id") String id);

        @Query(value = "SELECT * "
                        + "FROM transaction_receive "
                        + "WHERE trace_id = :id "
                        + "AND status = 0", nativeQuery = true)
        TransactionReceiveEntity getTransactionByTraceId(@Param(value = "id") String id);
}
