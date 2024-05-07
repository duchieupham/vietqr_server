package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.GoogleChatBankDTO;
import com.vietqr.org.dto.LarkBankDTO;
import com.vietqr.org.entity.GoogleChatAccountBankEntity;

@Repository
public interface GoogleChatAccountBankRepository extends JpaRepository<GoogleChatAccountBankEntity, Long> {

    @Query(value = "SELECT bank_id FROM google_chat_account_bank "
            + "WHERE bank_id = :bankId "
            + "AND google_chat_id = :googleChatId "
            + "LIMIT 1", nativeQuery = true)
    String checkExistedBankId(@Param(value = "bankId") String bankId,
            @Param(value = "googleChatId") String googleChatId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM google_chat_account_bank "
            + "WHERE bank_id = :bankId "
            + "AND google_chat_id = :googleChatId ", nativeQuery = true)
    void deleteByBankIdAndGoogleChatId(@Param(value = "bankId") String bankId,
            @Param(value = "googleChatId") String googleChatId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM google_chat_account_bank "
            + "WHERE google_chat_id = :googleChatId ", nativeQuery = true)
    void deleteByGoogleChatId(@Param(value = "googleChatId") String googleChatId);

    @Query(value = "SELECT a.id as googleChatBankId, a.bank_id as bankId, b.bank_account as bankAccount, c.bank_short_name as bankShortName, c.bank_code as bankCode, b.bank_account_name as userBankName, c.img_id as imgId "
            + "FROM google_chat_account_bank a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.google_chat_id = :googleChatId ", nativeQuery = true)
    List<GoogleChatBankDTO> getGoogleAccountBanks(@Param(value = "googleChatId") String googleChatId);

}
