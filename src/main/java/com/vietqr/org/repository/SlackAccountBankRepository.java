package com.vietqr.org.repository;

import com.vietqr.org.dto.SlackBankDTO;
import com.vietqr.org.entity.SlackAccountBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Repository
public interface SlackAccountBankRepository  extends JpaRepository<SlackAccountBankEntity, String> {
    @Query(value = "SELECT bank_id FROM slack_account_bank "
            + "WHERE bank_id = :bankId "
            + "AND slack_id = :slackId "
            + "LIMIT 1", nativeQuery = true)
    String checkExistedBankId(@Param(value = "bankId") String bankId, @Param(value = "slackId") String slackId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM slack_account_bank "
            + "WHERE bank_id = :bankId "
            + "AND slack_id = :slackId ", nativeQuery = true)
    void deleteByBankIdAndSlackId(@Param(value = "bankId") String bankId, @Param(value = "slackId") String slackId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM slack_account_bank "
            + "WHERE slack_id = :slackId ", nativeQuery = true)
    void deleteBySlackId(@Param(value = "slackId") String slackId);

    @Query(value = "SELECT a.id as slackBankId, a.bank_id as bankId, b.bank_account as bankAccount, c.bank_short_name as bankShortName, c.bank_code as bankCode, b.bank_account_name as userBankName, c.img_id as imgId "
            + "FROM slack_account_bank a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id = c.id "
            + "WHERE a.slack_id = :slackId ", nativeQuery = true)
    List<SlackBankDTO> getSlackAccountBanks(@Param(value = "slackId") String slackId);

    @Query(value = "SELECT webhook FROM slack_account_bank WHERE bank_id = :bankId", nativeQuery = true)
    List<String> getWebhooksByBankId(String bankId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE slack_account_bank SET webhook = :webhook WHERE slack_id = :slackId ", nativeQuery = true)
    void updateWebHookSlack(@Param("webhook") String webhook, @Param("slackId") String slackId);
}
