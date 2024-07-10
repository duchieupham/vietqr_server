package com.vietqr.org.repository;

import com.vietqr.org.dto.GoogleSheetBankDTO;
import com.vietqr.org.entity.GoogleSheetAccountBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface GoogleSheetAccountBankRepository extends JpaRepository<GoogleSheetAccountBankEntity, String> {

    @Query(value = "SELECT bank_id FROM google_sheet_account_bank WHERE bank_id = :bankId AND google_sheet_id = :googleSheetId LIMIT 1", nativeQuery = true)
    String checkExistedBankId(@Param(value = "bankId") String bankId, @Param(value = "googleSheetId") String googleSheetId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM google_sheet_account_bank WHERE bank_id = :bankId AND google_sheet_id = :googleSheetId", nativeQuery = true)
    void deleteByBankIdAndGoogleSheetId(@Param(value = "bankId") String bankId, @Param(value = "googleSheetId") String googleSheetId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM google_sheet_account_bank WHERE google_sheet_id = :googleSheetId", nativeQuery = true)
    void deleteByGoogleSheetId(@Param(value = "googleSheetId") String googleSheetId);

    @Query(value = "SELECT gsab.id as googleSheetAccountBankId, gsab.bank_id as bankId, abr.bank_account as bankAccount, bt.bank_short_name as bankShortName, bt.bank_code as bankCode, abr.bank_account_name as userBankName, bt.img_id as imgId " +
            "FROM google_sheet_account_bank gsab " +
            "INNER JOIN account_bank_receive abr ON gsab.bank_id = abr.id " +
            "INNER JOIN bank_type bt ON abr.bank_type_id = bt.id " +
            "WHERE gsab.google_sheet_id = :googleSheetId", nativeQuery = true)
    List<GoogleSheetBankDTO> getGoogleSheetAccountBanks(@Param("googleSheetId") String googleSheetId);

    @Query(value = "SELECT webhook FROM google_sheet_account_bank WHERE bank_id = :bankId", nativeQuery = true)
    List<String> getWebhooksByBankId(String bankId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE google_sheet_account_bank SET webhook = :webhook WHERE google_sheet_id = :googleSheetId", nativeQuery = true)
    void updateWebHookGoogleSheet(String webhook, String googleSheetId);
}