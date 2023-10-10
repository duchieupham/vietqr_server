package com.vietqr.org.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankSmsDTO;
import com.vietqr.org.dto.AccountBankSmsDetailDTO;
import com.vietqr.org.entity.AccountBankSmsEntity;

@Repository
public interface AccountBankSmsRepository extends JpaRepository<AccountBankSmsEntity, Long> {

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.bank_account_name as bankAccountName, a.bank_type_id as bankTypeId, b.bank_name as bankName, "
                        + "b.bank_code as bankCode, b.bank_short_name as bankShortName, a.sms_id as smsId, b.img_id as imgId, "
                        + "a.is_sync as sync, a.is_linked as linked, a.status "
                        + "FROM account_bank_sms a "
                        + "INNER JOIN bank_type b "
                        + "ON a.bank_type_id = b.id "
                        + "WHERE a.sms_id = :smsId ", nativeQuery = true)
        List<AccountBankSmsDTO> getListBankAccountSmsBySmsId(@Param(value = "smsId") String smsId);

        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.bank_account_name as bankAccountName, a.bank_type_id as bankTypeId, b.bank_name as bankName, "
                        + "b.bank_code as bankCode, b.bank_short_name as bankShortName, a.sms_id as smsId, b.img_id as imgId, "
                        + "a.is_sync as sync, a.is_linked as linked, a.status "
                        + "FROM account_bank_sms a "
                        + "INNER JOIN bank_type b "
                        + "ON a.bank_type_id = b.id "
                        + "WHERE a.id = :id ", nativeQuery = true)
        AccountBankSmsDTO getBankAccountSmsById(@Param(value = "id") String id);

        // get detail
        // id, bankAccount, userBankName, bankTypeId, smsId, status, type,
        // bankShortName, bankCode, bankName, imgId
        @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.bank_account_name as userBankName, "
                        + "a.bank_type_id as bankTypeId, a.sms_id as smsId, a.status, a.type "
                        + "b.bank_short_name as bankShortName, b.bank_code as bankCode, b.bank_name as bankName, b.img_id as imgId "
                        + "FROM account_bank_sms a "
                        + "INNER JOIN bank_type b "
                        + "ON a.bank_type_id = b.id "
                        + "WHERE a.id = :id ", nativeQuery = true)
        AccountBankSmsDetailDTO getAccountBankSmsDetail(@Param(value = "id") String id);

}
