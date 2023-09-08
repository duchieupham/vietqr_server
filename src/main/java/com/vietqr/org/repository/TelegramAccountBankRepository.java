package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.TelBankDTO;
import com.vietqr.org.entity.TelegramAccountBankEntity;

@Repository
public interface TelegramAccountBankRepository extends JpaRepository<TelegramAccountBankEntity, Long> {

    @Query(value = "SELECT a.id as telBankId, a.bank_id as bankId, b.bank_account as bankAccount, c.bank_short_name as bankShortName, c.bank_code as bankCode, b.bank_account_name as userBankName, c.img_id as imgId "
            + "FROM telegram_account_bank a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.telegram_id = :telId ", nativeQuery = true)
    List<TelBankDTO> getTelAccBanksByTelId(@Param(value = "telId") String telId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM telegram_account_bank WHERE telegram_id = :telId", nativeQuery = true)
    void removeTelAccBankByTelId(@Param(value = "telId") String telId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM telegram_account_bank WHERE telegram_id = :telId AND bank_id = :bankId ", nativeQuery = true)
    void removeTelAccBankByTelIdAndBankId(@Param(value = "telId") String telId, @Param(value = "bankId") String bankId);

    @Query(value = "SELECT chat_id FROM telegram_account_bank WHERE bank_id = :bankId", nativeQuery = true)
    List<String> getChatIdsByBankId(@Param(value = "bankId") String bankId);

}
