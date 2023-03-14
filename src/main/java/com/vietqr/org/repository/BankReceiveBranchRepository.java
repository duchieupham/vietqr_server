package com.vietqr.org.repository;

import javax.transaction.Transactional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vietqr.org.entity.BankReceiveBranchEntity;
import com.vietqr.org.dto.AccountBankReceivePersonalDTO;

@Repository
public interface BankReceiveBranchRepository extends JpaRepository<BankReceiveBranchEntity, Long> {

    @Query(value = "SELECT a.bank_id as bankId, b.bank_account as bankAccount, b.bank_account_name as userBankName, c.bank_name as bankName, "
            + "c.bank_code as bankCode, c.img_id as imgId, b.type as bankType, a.branch_id as branchId, d.business_id as businessId, d.name as branchName, "
            + "e.name as businessName, d.code as branchCode, e.code as businessCode "
            + "FROM bank_receive_branch a "
            + "INNER JOIN account_bank_receive b "
            + "ON a.bank_id = b.id "
            + "INNER JOIN bank_type c "
            + "ON b.bank_type_id= c.id "
            + "INNER JOIN branch_information d "
            + "ON a.branch_id = d.id "
            + "INNER JOIN business_information e "
            + "ON d.business_id = e.id "
            + "WHERE a.branch_id = :branchId", nativeQuery = true)
    List<AccountBankReceivePersonalDTO> getBankReceiveBranchs(@Param(value = "branchId") String businessId);

    // delete
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM bank_receive_branch WHERE id = :id", nativeQuery = true)
    void deleteBankReceiveBranch(@Param(value = "id") String id);
}
