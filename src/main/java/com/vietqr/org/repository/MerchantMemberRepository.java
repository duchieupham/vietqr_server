package com.vietqr.org.repository;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantMemberRepository extends JpaRepository<MerchantMemberEntity, String> {
    @Query(value = "SELECT a.id AS id, a.phone_no AS phoneNo "
            + "b.middle_name AS middleName, b.last_name AS lastName, "
            + "b.first_name AS firstName, b.img_id AS imgId "
            + "FROM account_login a "
            + "INNER JOIN account_information b ON a.id = "
            + "WHERE a.id AS id, a.phone_no AS phoneNo", nativeQuery = true)
    List<AccountMemberDTO> getMerchantMembersByUserId(String merchantId);

    @Query(value = "SELECT a.id FROM merchant_member a WHERE a.id = :merchantId AND a.user_id = :id", nativeQuery = true)
    String checkUserExistedFromMerchant(String merchantId, String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_member "
            + "WHERE merchant_id = :merchantId AND user_id = :userId", nativeQuery = true)
    void removeMemberFromMerchant(String merchantId, String userId);
}
