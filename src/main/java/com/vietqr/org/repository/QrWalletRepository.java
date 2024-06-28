//package com.vietqr.org.repository;
//
//import com.vietqr.org.dto.qrfeed.IListQrWalletDTO;
//import com.vietqr.org.entity.qrfeed.QrWalletEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import javax.transaction.Transactional;
//import java.util.List;
//
//@Repository
//public interface QrWalletRepository extends JpaRepository<QrWalletEntity, String> {
//
//    @Query(value = "SELECT a.id AS id, a.description AS description, a.is_public AS isPublic, a.qr_type as QrType, " +
//            "a.time_created as timeCreate, " +
//            "a.title AS title, a.value as content, b.role AS role " +
//            "FROM viet_qr.qr_wallet a " +
//            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
//            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
//            "GROUP BY a.id, b.role " +
//            "ORDER BY time_created DESC " +
//            "LIMIT :offset, :size  ", nativeQuery = true)
//    List<IListQrWalletDTO> getQrWallets(String value, int offset, int size);
//
//    @Query(value = "SELECT COUNT(a.id) " +
//            "FROM viet_qr.qr_wallet a " +
//            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
//            "ORDER BY time_created DESC ", nativeQuery = true)
//    int countQrWallet(String value);
//
//    @Query(value = "SELECT a.id AS id, a.description AS description, a.is_public AS isPublic, a.qr_type as QrType, " +
//            "a.time_created as timeCreate, a.title AS title, a.value as content, b.role AS role " +
//            "FROM viet_qr.qr_wallet a " +
//            "WHERE a.user_id = :value AND (a.qr_type = 'QR Link') OR (a.qr_type = 'QR Text') " +
//            "ORDER BY time_created DESC ", nativeQuery = true)
//    IListQrWalletDTO getQrLinkOrQrTextByUserId(String value);
//
//    @Query(value = "SELECT a.* FROM viet_qr.qr_wallet a WHERE a.id = :qrId ", nativeQuery = true)
//    QrWalletEntity getQrLinkOrQrTextById(String qrId);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE qr_wallet SET description = :description, is_public = :isPublic, " +
//            "qr_type = :qrType, title = :title, value = :content WHERE id = :id ", nativeQuery = true)
//    void updateQrWallet(String id, String description, int isPublic, String qrType, String title, String content);
//
//    @Modifying
//    @Transactional
//    @Query(value = "DELETE FROM qr_wallet WHERE id IN :ids", nativeQuery = true)
//    void deleteByIds(@Param("ids") List<String> ids);
//
//    @Query(value = "SELECT id FROM qr_wallet WHERE id IN :ids", nativeQuery = true)
//    List<String> findExistingIds(@Param("ids") List<String> ids);
//
//
////    @Query(value = "SELECT COUNT(a.id) " +
////            "FROM viet_qr.qr_wallet a " +
////            "INNER JOIN qr_user b ON a.user_id =  b.user_id " +
////            "WHERE (a.description LIKE %:value%) OR (a.title LIKE %:value%) " +
////            "GROUP BY a.id, b.role " +
////            "ORDER BY time_created DESC ", nativeQuery = true)
////    int countQrWallet(String value);
//
//
//}
