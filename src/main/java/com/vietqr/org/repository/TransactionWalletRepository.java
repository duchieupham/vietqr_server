package com.vietqr.org.repository;

import java.util.List;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.vietqr.org.dto.TransWalletListDTO;
import com.vietqr.org.dto.TransactionVNPTItemDTO;
import com.vietqr.org.dto.VNPTEpayTransCounterDTO;
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

        @Query(value = "SELECT id, amount, bill_number as billNumber, status, trans_type as transType, payment_type as paymentType, payment_method as paymentMethod, "
                        + "time_created AS timeCreated, "
                        + "time_paid AS timePaid "
                        + "FROM transaction_wallet "
                        + "WHERE user_id = :userId "
                        + "ORDER BY time_created DESC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<TransWalletListDTO> getTransactionWalletList(@Param(value = "userId") String userId,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT id, amount, bill_number as billNumber, status, trans_type as transType, payment_type as paymentType, payment_method as paymentMethod, "
                        + "time_created AS timeCreated, "
                        + "time_paid AS timePaid "
                        + "FROM transaction_wallet "
                        + "WHERE user_id = :userId AND status = :status "
                        + "ORDER BY time_created DESC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<TransWalletListDTO> getTransactionWalletListByStatus(@Param(value = "userId") String userId,
                        @Param(value = "status") int status,
                        @Param(value = "offset") int offset);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_wallet SET status = :status, time_paid = :timePaid WHERE id = :id", nativeQuery = true)
        void updateTransactionWalletStatus(@Param(value = "status") int status,
                        @Param(value = "timePaid") long timePaid, @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_wallet SET status = :status, time_paid = :timePaid, amount = :amount, phone_norc = :phoneNoRC WHERE user_id = :userId AND otp = :otp AND payment_type = :paymentType AND status = 0", nativeQuery = true)
        void updateTransactionWallet(@Param(value = "status") int status,
                        @Param(value = "timePaid") long timePaid,
                        @Param(value = "amount") String amount,
                        @Param(value = "phoneNoRC") String phoneNoRC,
                        @Param(value = "userId") String userId,
                        @Param(value = "otp") String otp,
                        @Param(value = "paymentType") int paymentType);

        @Transactional
        @Modifying
        @Query(value = "UPDATE transaction_wallet SET time_created = :timeCreated, amount = :amount "
                + "WHERE user_id = :userId AND otp = :otp AND payment_type = :paymentType AND status = 0", nativeQuery = true)
        void updateTransactionWalletConfirm(
                        @Param(value = "timeCreated") long timeCreated,
                        @Param(value = "amount") String amount,
                        @Param(value = "userId") String userId,
                        @Param(value = "otp") String otp,
                        @Param(value = "paymentType") int paymentType);

        @Query(value = "SELECT id FROM transaction_wallet WHERE otp = :otp AND user_id = :userId AND payment_type = :paymentType AND status = 0", nativeQuery = true)
        String checkExistedTransactionnWallet(@Param(value = "otp") String otp,
                        @Param(value = "userId") String userId,
                        @Param(value = "paymentType") int paymentType);

        @Query(value = "SELECT a.id, a.amount, a.bill_number AS billNumber, a.content, a.status, a.time_created as timeCreated, a.time_paid as timePaid, "
                        + "a.user_id as userId, a.payment_method as paymentMethod, a.phone_norc as phoneNorc,  "
                        + "b.phone_no as phoneNo,  CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName  "
                        + "FROM transaction_wallet a  "
                        + "INNER JOIN account_login b  "
                        + "ON a.user_id = b.id  "
                        + "INNER JOIN account_information c  "
                        + "ON a.user_id = c.user_id  "
                        + "WHERE a.payment_type = 1  "
                        + "ORDER BY a.time_created DESC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<TransactionVNPTItemDTO> getTransactionsVNPT(
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT a.id, a.amount, a.bill_number AS billNumber, a.content, a.status, a.time_created as timeCreated, a.time_paid as timePaid, "
                        + "a.user_id as userId, a.payment_method as paymentMethod, a.phone_norc as phoneNorc,  "
                        + "b.phone_no as phoneNo,  CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName  "
                        + "FROM transaction_wallet a  "
                        + "INNER JOIN account_login b  "
                        + "ON a.user_id = b.id  "
                        + "INNER JOIN account_information c  "
                        + "ON a.user_id = c.user_id  "
                        + "WHERE a.payment_type = 1 AND a.status = :status "
                        + "ORDER BY a.time_created DESC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<TransactionVNPTItemDTO> getTransactionsVNPTFilter(
                        @Param(value = "status") int status,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT COUNT(*) as totalTrans, SUM(amount) as totalAmount "
                        + "FROM transaction_wallet  "
                        + "WHERE payment_type = 1 AND status = 1  ", nativeQuery = true)
        VNPTEpayTransCounterDTO getVNPTEpayCounter();

}
