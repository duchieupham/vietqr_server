package com.vietqr.org.repository;

import com.vietqr.org.dto.AccountBankReceiveShareDTO;
import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.AccountBankReceiveShareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AccountBankReceiveShareRepository
                extends JpaRepository<AccountBankReceiveShareEntity, Long> {

        @Query(value = "SELECT a.bank_id as bankId, b.bank_account as bankAccount, b.bank_account_name as userBankName, "
                        +
                        "c.bank_name as bankName, c.bank_short_name as bankShortName, c.bank_code as bankCode, c.img_id as imgId, b.type as bankType, b.is_authenticated as authenticated, "
                        +
                        "b.user_id as userId, a.is_owner as isOwner , c.status as bankTypeStatus, c.id as bankTypeId "
                        + "FROM account_bank_receive_share a "
                        + "INNER JOIN account_bank_receive b "
                        + "ON a.bank_id = b.id "
                        + "INNER JOIN bank_type c "
                        + "ON b.bank_type_id = c.id "
                        + "WHERE a.user_id = :userId", nativeQuery = true)
        List<AccountBankReceiveShareDTO> getAccountBankReceiveShare(@Param(value = "userId") String userId);

        @Query(value = "SELECT a.id, a.phone_no as phoneNo, b.first_name as firstName, b.middle_name as middleName, b.last_name as lastName, b.img_id as imgId, c.is_owner as isOwner "
                        + "FROM account_login a "
                        + "INNER JOIN account_information b "
                        + "ON a.id = b.user_id "
                        + "INNER JOIN account_bank_receive_share c "
                        + "ON c.user_id = a.id "
                        + "WHERE a.status = 1 AND c.bank_id = :bankId", nativeQuery = true)
        List<AccountMemberDTO> getMembersFromBank(@Param(value = "bankId") String bankId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM account_bank_receive_share WHERE user_id = :userId AND bank_id = :bankId", nativeQuery = true)
        void removeMemberFromBank(@Param(value = "userId") String userId,
                        @Param(value = "bankId") String bankId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM account_bank_receive_share WHERE bank_id = :bankId AND is_owner = false", nativeQuery = true)
        void removeAllMemberFromBank(@Param(value = "bankId") String bankId);

        // check member existed in account bank receive share
        @Query(value = "SELECT user_id as userId "
                        + "FROM account_bank_receive_share "
                        + "WHERE bank_id = :bankId "
                        + "AND user_id = :userId ", nativeQuery = true)
        String checkUserExistedFromBank(@Param(value = "userId") String userId,
                        @Param(value = "bankId") String bankId);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM account_bank_receive_share WHERE bank_id = :bankId", nativeQuery = true)
        void deleteAccountBankReceiveShareByBankId(String bankId);
}
