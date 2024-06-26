package com.vietqr.org.repository;

import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
import com.vietqr.org.entity.qrfeed.QrWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrWalletRepository extends JpaRepository<QrWalletEntity, String> {

    @Query(value = "SELECT a.id AS id, a.description AS description, a.is_public AS isPublic, a.qr_type as QrType, " +
            "a.time_created as timeCreate, " +
            "a.title AS title, a.value as content, b.role AS role " +
            "FROM viet_qr.qr_wallet a " +
            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
            "GROUP BY a.id, b.role " +
            "ORDER BY time_created DESC " +
            "LIMIT :offset, :size  ", nativeQuery = true)
    List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);

    @Query(value = "SELECT COUNT(a.id) " +
            "FROM viet_qr.qr_wallet a " +
            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
            "ORDER BY time_created DESC ", nativeQuery = true)
    int countQrWallet(String value);

//    @Query(value = "SELECT COUNT(a.id) " +
//            "FROM viet_qr.qr_wallet a " +
//            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
//            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
//            "GROUP BY a.id, b.role " +
//            "ORDER BY time_created DESC ", nativeQuery = true)
//    int countQrWallet(String value);


}
