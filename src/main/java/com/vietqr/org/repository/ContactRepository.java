package com.vietqr.org.repository;

import javax.transaction.Transactional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vietqr.org.dto.ContactRechargeDTO;
import com.vietqr.org.entity.ContactEntity;

@Repository
public interface ContactRepository extends JpaRepository<ContactEntity, Long> {

        @Query(value = "SELECT * FROM contact WHERE user_id = :userId AND status = 0 ORDER BY nickname ASC ", nativeQuery = true)
        List<ContactEntity> getContactApprovedByUserId(@Param(value = "userId") String userId);

        // contact approved all with pagging
        @Query(value = "SELECT * FROM contact "
                        + "WHERE user_id = :userId AND status = 0 "
                        + "ORDER BY nickname ASC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<ContactEntity> getContactApprovedByUserIdWithPagging(@Param(value = "userId") String userId,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT * FROM contact "
                        + "WHERE relation = 1 AND status = 0 "
                        + "ORDER BY nickname ASC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<ContactEntity> getContactPublicByUserIdWithPagging(
                        @Param(value = "offset") int offset);

        // contact approved by status with pagging
        @Query(value = "SELECT * FROM contact "
                        + "WHERE user_id = :userId AND status = 0 "
                        + "AND TYPE = :type "
                        + "ORDER BY nickname ASC "
                        + "LIMIT :offset, 20", nativeQuery = true)
        List<ContactEntity> getContactApprovedByUserIdAndStatusWithPagging(@Param(value = "userId") String userId,
                        @Param(value = "type") int type,
                        @Param(value = "offset") int offset);

        @Query(value = "SELECT * FROM contact WHERE user_id = :userId AND status = 1 ORDER BY nickname ASC ", nativeQuery = true)
        List<ContactEntity> getContactPendingByUserId(@Param(value = "userId") String userId);

        @Query(value = "SELECT id FROM contact WHERE user_id = :userId AND value = :value AND type = :type ", nativeQuery = true)
        String checkExistedRecord(@Param(value = "userId") String userId,
                        @Param(value = "value") String value,
                        @Param(value = "type") int type);

        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET status = :status WHERE id = :id ", nativeQuery = true)
        void updateContactStatus(@Param(value = "status") int status,
                        @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM contact WHERE id = :id ", nativeQuery = true)
        void deleteContactById(@Param(value = "id") String id);

        @Query(value = "SELECT * FROM contact WHERE id = :id ", nativeQuery = true)
        ContactEntity getContactById(@Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET nickname = :nickname, type = :type, additional_data = :additionalData "
                        + "WHERE id = :id ", nativeQuery = true)
        void udpateContact(@Param(value = "nickname") String nickname,
                        @Param(value = "type") int type,
                        @Param(value = "additionalData") String additionalData,
                        @Param(value = "id") String id);

        // update contact color type = 4
        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET nickname = :nickname, additional_data = :note, color_type = :colorType, "
                        + "address = :address, company = :company, email = :email, "
                        + "phone_no = :phoneNo, website = :website, value = :value "
                        + "WHERE id = :id", nativeQuery = true)
        void updateContactVcard(
                        @Param(value = "nickname") String nickname,
                        @Param(value = "note") String note,
                        @Param(value = "colorType") int colorType,
                        @Param(value = "address") String address,
                        @Param(value = "company") String conpany,
                        @Param(value = "email") String email,
                        @Param(value = "phoneNo") String phoneNo,
                        @Param(value = "website") String website,
                        @Param(value = "value") String value,
                        @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET nickname = :nickname, additional_data = :additionalData, color_type = :colorType "
                        + "WHERE id = :id ", nativeQuery = true)
        void udpateContactMultipart(@Param(value = "nickname") String nickname,
                        @Param(value = "additionalData") String additionalData,
                        @Param(value = "colorType") int colorType,
                        @Param(value = "id") String id);

        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET img_id = :imgId WHERE id = :id", nativeQuery = true)
        void updateImgIdById(@Param(value = "imgId") String imgId,
                        @Param(value = "id") String id);

        @Query(value = "SELECT c.user_id as userId, a.nickname, c.img_id as imgId, d.phone_no as phoneNo, c.carrier_type_id as carrierTypeId "
                        + "FROM contact a "
                        + "INNER JOIN account_wallet b "
                        + "ON a.value = b.wallet_id "
                        + "INNER JOIN account_information c "
                        + "ON b.user_id = c.user_id "
                        + "INNER JOIN account_login d "
                        + "ON c.user_id = d.id "
                        + "WHERE a.type = 1 AND a.user_id = :userId "
                        + "ORDER BY a.nickname ASC ", nativeQuery = true)
        List<ContactRechargeDTO> getContactRecharge(@Param(value = "userId") String userId);

        @Query(value = "SELECT b.img_id "
                        + "FROM account_wallet a "
                        + "INNER JOIN account_information b "
                        + "ON a.user_id = b.user_id "
                        + "WHERE a.wallet_id = :walletId", nativeQuery = true)
        String getImgIdByWalletId(@Param(value = "walletId") String walletId);

        @Transactional
        @Modifying
        @Query(value = "UPDATE contact SET relation = :relation WHERE id = :id ", nativeQuery = true)
        void updateContactRelation(@Param(value = "relation") int relation, @Param(value = "id") String id);
}
