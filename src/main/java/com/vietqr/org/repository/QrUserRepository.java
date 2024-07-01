package com.vietqr.org.repository;

import com.vietqr.org.entity.qrfeed.QrUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface QrUserRepository extends JpaRepository<QrUserEntity, String> {
    @Modifying
    @Transactional
    @Query(value="UPDATE qr_user SET role = :role WHERE user_id = :userId AND qr_wallet_id = :qrWalletId",nativeQuery = true)
    void updateUserRole(@Param("userId") String userId, @Param("qrWalletId") String qrWalletId, @Param("role") String role);
}
