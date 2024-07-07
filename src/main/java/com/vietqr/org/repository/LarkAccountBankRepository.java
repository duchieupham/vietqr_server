package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.LarkBankDTO;
import com.vietqr.org.entity.LarkAccountBankEntity;

@Repository
public interface LarkAccountBankRepository extends JpaRepository<LarkAccountBankEntity, Long> {

        @Query(value = "SELECT a.id as larkBankId, a.bank_id as bankId, b.bank_account as bankAccount, c.bank_short_name as bankShortName, c.bank_code as bankCode, b.bank_account_name as userBankName, c.img_id as imgId "
                        + "FROM lark_account_bank a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.lark_id = :larkId ", nativeQuery = true)
        List<LarkBankDTO> getLarkAccBanksByLarkId(@Param(value = "larkId") String larkId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM lark_account_bank WHERE lark_id = :larkId", nativeQuery = true)
        void removeLarkAccBankByLarkId(@Param(value = "larkId") String larkId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM lark_account_bank WHERE lark_id = :larkId AND bank_id = :bankId ", nativeQuery = true)
        void removeLarkAccBankByLarkIdAndBankId(@Param(value = "larkId") String larkId,
                        @Param(value = "bankId") String bankId);

        @Query(value = "SELECT webhook FROM lark_account_bank WHERE bank_id = :bankId", nativeQuery = true)
        List<String> getWebhooksByBankId(@Param(value = "bankId") String bankId);

        @Transactional
        @Modifying
        @Query(value = "UPDATE lark_account_bank SET webhook = :webhook WHERE lark_id = :larkId ", nativeQuery = true)
    void updateWebhook(String webhook, String larkId);
}
