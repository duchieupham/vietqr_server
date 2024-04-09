package com.vietqr.org.repository;

import com.vietqr.org.dto.BankActiveAdminDataDTO;
import com.vietqr.org.entity.BankReceiveActiveHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankReceiveActiveHistoryRepository extends JpaRepository<BankReceiveActiveHistoryEntity, String> {
    @Query(value = "SELECT a.bank_id AS bankId, d.bank_account AS bankAccount, "
            + "e.bank_name AS bankName, d.bank_account_name AS userBankName, "
            + "e.bank_short_name AS bankShortName, a.valid_fee_from AS validFeeFrom, "
            + "a.valid_fee_to AS validFeeTo, a.user_id AS userId, "
            + "b.phone_no AS phoneNo, COALESCE(b.email, '') AS email, "
            + "CONCAT(c.last_name, ' ' ,c.middle_name, ' ' , c.first_name) AS fullName "
            + "FROM bank_receive_active_history a "
            + "INNER JOIN account_login b ON a.user_id = b.id "
            + "INNER JOIN account_information c ON c.user_id = a.user_id "
            + "INNER JOIN account_bank_receive d ON d.id = a.bank_id "
            + "INNER JOIN bank_type e ON e.id = d.bank_type_id "
            + "WHERE a.key_active = :keyActive LIMIT 1 ", nativeQuery = true)
    BankActiveAdminDataDTO getBankActiveAdminData(String keyActive);
}
