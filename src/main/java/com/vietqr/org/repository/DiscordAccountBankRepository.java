package com.vietqr.org.repository;

import com.vietqr.org.dto.DiscordBankDTO;
import com.vietqr.org.entity.DiscordAccountBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DiscordAccountBankRepository extends JpaRepository<DiscordAccountBankEntity, String> {

    @Query(value = "SELECT bank_id FROM discord_account_bank WHERE bank_id = :bankId AND discord_id = :discordId LIMIT 1", nativeQuery = true)
    String checkExistedBankId(@Param(value = "bankId") String bankId, @Param(value = "discordId") String discordId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM discord_account_bank WHERE bank_id = :bankId AND discord_id = :discordId", nativeQuery = true)
    void deleteByBankIdAndDiscordId(@Param(value = "bankId") String bankId, @Param(value = "discordId") String discordId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM discord_account_bank WHERE discord_id = :discordId", nativeQuery = true)
    void deleteByDiscordId(@Param(value = "discordId") String discordId);

    @Query(value = "SELECT dab.id as discordAccountBankId, dab.bank_id as bankId, abr.bank_account as bankAccount, bt.bank_short_name as bankShortName, bt.bank_code as bankCode, abr.bank_account_name as userBankName, bt.img_id as imgId " +
            "FROM discord_account_bank dab " +
            "INNER JOIN account_bank_receive abr ON dab.bank_id = abr.id " +
            "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id " +
            "WHERE dab.discord_id = :discordId", nativeQuery = true)
    List<DiscordBankDTO> getDiscordAccountBanks(@Param("discordId") String discordId);

    @Query(value = "SELECT webhook FROM discord_account_bank WHERE bank_id = :bankId", nativeQuery = true)
    List<String> getWebhooksByBankId(String bankId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE discord_account_bank SET webhook = :webhook WHERE discord_id = :discordId", nativeQuery = true)
    void updateWebHookDiscord(String webhook, String discordId);
}