package com.vietqr.org.repository;

import com.vietqr.org.entity.MerchantMemberRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

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
}
