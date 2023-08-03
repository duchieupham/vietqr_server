package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vietqr.org.entity.TransactionWalletEntity;

@Repository
public interface TransactionWalletRepository extends JpaRepository<TransactionWalletEntity, Long> {

    @Query(value = "SELECT * FROM transaction_wallet WHERE id = :id", nativeQuery = true)
    TransactionWalletEntity getTransactionWalletById(@Param(value = "id") String id);

    @Query(value = "SELECT * FROM transaction_wallet WHERE bill_number = :billNumber AND status = 0", nativeQuery = true)
    TransactionWalletEntity getTransactionWalletByBillNumber(@Param(value = "billNumber") String billNumber);

    @Query(value = "SELECT * FROM transaction_wallet WHERE user_id = :userId ORDER BY time_created DESC", nativeQuery = true)
    List<TransactionWalletEntity> getTransactionWalletsByUserId(@Param(value = "userId") String userId);

    @Query(value = "SELECT * FROM transaction_wallet WHERE ORDER BY time_created DESC", nativeQuery = true)
    List<TransactionWalletEntity> getTransactionWallets();

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_wallet SET status = :status, time_paid = :timePaid WHERE id = :id", nativeQuery = true)
    void updateTransactionWalletStatus(@Param(value = "status") int status,
            @Param(value = "timePaid") long timePaid, @Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE transaction_wallet SET status = :status, time_paid = :timePaid, amount = :amount WHERE user_id = :userId AND otp = :otp AND payment_type = :paymentType AND status = 0", nativeQuery = true)
    void updateTransactionWallet(@Param(value = "status") int status,
            @Param(value = "timePaid") long timePaid,
            @Param(value = "amount") String amount,
            @Param(value = "userId") String userId,
            @Param(value = "otp") String otp,
            @Param(value = "paymentType") int paymentType);

    @Query(value = "SELECT id FROM transaction_wallet WHERE otp = :otp AND user_id = :userId AND payment_type = :paymentType AND status = 0", nativeQuery = true)
    String checkExistedTransactionnWallet(@Param(value = "otp") String otp,
            @Param(value = "userId") String userId,
            @Param(value = "paymentType") int paymentType);
}
