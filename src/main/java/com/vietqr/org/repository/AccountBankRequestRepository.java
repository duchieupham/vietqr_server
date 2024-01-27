package com.vietqr.org.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.AccountBankRequestItemDTO;
import com.vietqr.org.entity.AccountBankRequestEntity;

@Repository
public interface AccountBankRequestRepository extends JpaRepository<AccountBankRequestEntity, Long> {

    @Query(value = "SELECT a.id, a.bank_account as bankAccount, a.user_bank_name as userBankName, "
            + "a.bank_code as bankCode, a.national_id as nationalId, a.phone_authenticated as phoneAuthenticated, "
            + "a.request_type as requestType, a.address, a.time_created as timeCreated, a.user_id as userId, "
            + "a.is_sync as isSync, c.first_name as firstName, c.middle_name as middleName, c.last_name as lastName, "
            + "b.phone_no as phoneNo "
            + "FROM account_bank_request a "
            + "LEFT JOIN account_login b "
            + "ON a.user_id = b.id "
            + "LEFT JOIN account_information c "
            + "ON a.user_id = c.user_id "
            + "ORDER BY time_created DESC LIMIT :offset, 20", nativeQuery = true)
    List<AccountBankRequestItemDTO> getAccountBankRequests(@Param(value = "offsett") int offset);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_bank_request "
            + "SET bank_account = :bankAccount, user_bank_name = :userBankName, "
            + "bank_code = :bankCode, "
            + "national_id = :nationalId, phone_authenticated = :phoneAuthenticated, "
            + "request_type = :requestType, address = :address "
            + "WHERE id = :id ", nativeQuery = true)
    void updateAccountBankRequest(
            @Param(value = "bankAccount") String bankAccount,
            @Param(value = "userBankName") String userBankName,
            @Param(value = "bankCode") String bankCode,
            @Param(value = "nationalId") String nationalId,
            @Param(value = "phoneAuthenticated") String phoneAuthenticated,
            @Param(value = "requestType") int requestType,
            @Param(value = "address") String address,
            @Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM account_bank_request "
            + "WHERE id = :id", nativeQuery = true)
    void deleteAccountBankRequest(@Param(value = "id") String id);
}
