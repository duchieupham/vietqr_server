package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantRoleRawDTO;
import com.vietqr.org.entity.MerchantMemberRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantMemberRoleRepository extends JpaRepository<MerchantMemberRoleEntity, String> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member_role "
            + "WHERE merchant_member_id = :merchantMemberId AND user_id = :userId", nativeQuery = true)
    void removeMerchantMemberRole(@Param(value = "merchantMemberId") String merchantMemberId,
                                  @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member_role "
            + "WHERE merchant_member_id IN ( "
            + "SELECT id "
            + "FROM merchant_member "
            + "WHERE merchant_id = :merchantId AND user_id = :userId) ", nativeQuery = true)
    void deleteMerchantMemberRoleByUserIdAndMerchantId(@Param(value = "merchantId") String merchantId,
                                                       @Param(value = "userId") String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member_role "
            + "WHERE merchant_member_id IN ( "
            + "SELECT id "
            + "FROM merchant_member "
            + "WHERE terminal_id = :terminalId AND user_id = :userId) ", nativeQuery = true)
    void deleteMerchantMemberRoleByUserIdAndTerminalId(String terminalId, String userId);

    @Query(value = "SELECT a.id "
            + "FROM merchant_member_role a "
            + "WHERE a.user_id = :userId AND a.trans_receive_role_ids RLIKE :rolesAccept "
            + "LIMIT 1", nativeQuery = true)
    String checkMemberHaveRole(String userId, String rolesAccept);

    @Query(value = "SELECT a.merchant_member_id AS merchantMemberId, "
            + "c.category AS category, c.role AS role "
            + "FROM merchant_member_role a "
            + "INNER JOIN transaction_receive_role c ON JSON_CONTAINS(a.trans_receive_role_ids, JSON_QUOTE(c.id)) "
            + "WHERE a.merchant_member_id IN (:merchantMemberIds) ", nativeQuery = true)
    List<IMerchantRoleRawDTO> getMerchantIdsByMerchantMemberIds(List<String> merchantMemberIds);

    @Query(value = "SELECT DISTINCT b.user_id "
            + "FROM merchant_member_role a "
            + "INNER JOIN merchant_member b ON a.merchant_member_id = b.id "
            + "INNER JOIN merchant_bank_receive c ON c.merchant_id = b.merchant_id "
            + "WHERE c.bank_id = :bankId AND a.trans_receive_role_ids RLIKE :roles ", nativeQuery = true)
    List<String> getListUserIdRoles(String bankId, String roles);

    @Query(value = "SELECT a.id "
            + "FROM transaction_receive_role a "
            + "WHERE EXISTS ( "
            + "SELECT 1 "
            + "FROM merchant_member_role b "
            + "INNER JOIN merchant_member c ON c.id = b.merchant_member_id "
            + "INNER JOIN merchant_bank_receive d ON d.merchant_id = c.merchant_id "
            + "WHERE b.trans_receive_role_ids LIKE CONCAT('%', a.id, '%') "
            + "AND c.user_id = :userId AND d.bank_id = :bankId ) ", nativeQuery = true)
    List<String> getRoleByUserIdAndBankId(String userId, String bankId);
}
