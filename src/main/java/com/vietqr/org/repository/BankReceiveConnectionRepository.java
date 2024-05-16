package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantBankMapperDTO;
import com.vietqr.org.entity.BankReceiveConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BankReceiveConnectionRepository extends JpaRepository<BankReceiveConnectionEntity, String> {
    @Query(value = "SELECT d.vso AS vso, d.name AS merchantName, "
            + "e.email AS email, e.phone_no AS phoneNo, "
            + "b.bank_account AS bankAccount, b.bank_account_name AS userBankName, "
            + "f.bank_short_name AS bankShortName "
            + "FROM bank_receive_connection a "
            + "INNER JOIN account_bank_receive b ON a.bank_id = b.id "
            + "INNER JOIN merchant_connection c ON c.id = a.mid_connect_id "
            + "INNER JOIN merchant_sync d ON d.id = c.mid "
            + "INNER JOIN account_login e ON e.id = b.user_id "
            + "INNER JOIN bank_type f ON f.id = b.bank_type_id "
            + "WHERE a.bank_id = :bankId AND c.mid = :merchantId ", nativeQuery = true)
    IMerchantBankMapperDTO getMerchantBankMapper(String merchantId, String bankId);
}
