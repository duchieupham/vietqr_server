package com.vietqr.org.repository;

import com.vietqr.org.dto.IMerchantMemberDTO;
import com.vietqr.org.dto.IMerchantMemberDetailDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantMemberRepository extends JpaRepository<MerchantMemberEntity, String> {

    @Query(value = "SELECT a.id FROM merchant_member a WHERE a.id = :merchantId AND a.user_id = :id LIMIT 1", nativeQuery = true)
    String checkUserExistedFromMerchant(String merchantId, String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member "
            + "WHERE merchant_id = :merchantId AND user_id = :userId", nativeQuery = true)
    void removeMemberFromMerchant(String merchantId, String userId);

    @Query(value = "SELECT DISTINCT a.merchant_id AS merchantId, a.user_id AS userId, "
            + "d.trans_receive_role_ids AS transReceiveRoles, "
            + "d.trans_refund_role_ids AS transRefundRoles, "
            + "b.phone_no AS phoneNo, "
            + "CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) AS fullName, "
            + "c.img_id AS imgId "
            + "FROM merchant_member a "
            + "INNER JOIN account_login b ON a.user_id = b.id "
            + "INNER JOIN account_information c ON a.user_id = c.user_id "
            + "INNER JOIN merchant_member_role d ON d.merchant_member_id = a.id "
            + "WHERE a.merchant_id = :merchantId AND a.is_active = TRUE "
            + "AND (b.phone_no LIKE %:value% OR CONCAT(c.last_name, ' ', c.middle_name, ' ', c.first_name) LIKE %:value%) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IMerchantMemberDTO> findMerchantMemberMerchantId(String merchantId, String value, int offset, int size);

    @Query(value = "SELECT a.terminal_id AS terminalId, b.id AS merchantMemberId, "
            + "b.trans_receive_role_ids AS transReceiveRoles, "
            + "b.trans_refund_role_ids AS TransRefundRoles "
            + "FROM merchant_member a "
            + "INNER JOIN merchant_member_role b ON b.merchant_member_id = a.id "
            + "WHERE a.merchant_id = :merchantId AND a.merchant_id != '' "
            + "AND a.user_id = :userId LIMIT 1 ", nativeQuery = true)
    IMerchantMemberDetailDTO getUserExistedFromMerchant(String merchantId, String userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member "
            + "WHERE merchant_id = :merchantId AND user_id = :userId ", nativeQuery = true)
    void deleteMerchantMemberByUserIdAndMerchantId(@Param(value = "merchantId") String merchantId,
                                                   @Param(value = "userId") String userId);

    @Query(value = "SELECT COUNT(DISTINCT a.merchant_id, a.user_id) "
            + "FROM merchant_member a WHERE a.merchant_id = :merchantId ", nativeQuery = true)
    int countMerchantMemberByMerchantId(String merchantId);
}
