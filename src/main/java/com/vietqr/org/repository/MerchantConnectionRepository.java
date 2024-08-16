package com.vietqr.org.repository;

import com.vietqr.org.entity.MerchantConnectionEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface MerchantConnectionRepository extends JpaRepository<MerchantConnectionEntity, Long> {

    @Query(value = "SELECT b.id "
            + "FROM merchant b "
            + "INNER JOIN account_customer_merchant c "
            + "ON c.merchant_id = b.id "
            + "INNER JOIN account_customer d "
            + "ON d.id = c.account_customer_id "
            + "WHERE d.username  = :username ", nativeQuery = true)
    List<String> checkExistedCustomerSyncByUsername(String username);

    @Query(value="SELECT COUNT(id) FROM merchant_connection", nativeQuery = true)
    Integer getCountingMerchantConnection();

    @Query(value = "SELECT id FROM merchant_connection WHERE mid = :mid ", nativeQuery = true)
    List<String> getIdMerchantConnectionByMid(String mid);

    @Query(value = "SELECT * FROM merchant_connection WHERE id = :id ", nativeQuery = true)
    MerchantConnectionEntity getMerchanConnectionById(String id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM merchant_connection WHERE id = :id", nativeQuery = true)
    void deleteMerchantConnectionById(@Param(value = "id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE merchant_connection " +
            "SET url_callback = :urlCallback, " +
            "url_get_token = :urlGetToken, " +
            "mid = :mid, " +
            "password = :password, " +
            "token = :token, " +
            "type = :type, " +
            "username = :username " +
            "WHERE id = :id", nativeQuery = true)
    void updateMerchantConnectionById(@Param("id") String id,
                                  @Param("urlCallback") String urlCallback,
                                  @Param("urlGetToken") String urlGetToken,
                                  @Param("mid") String mid,
                                  @Param("password") String password,
                                  @Param("token") String token,
                                  @Param("type") int type,
                                  @Param("username") String username);


    @Query(value = "SELECT * FROM merchant_connection LIMIT :offset, :size", nativeQuery = true)
    List<MerchantConnectionEntity> getAllMerchantConnection(@Param("offset") int offset, @Param("size") int size);

    @Query(value = "SELECT COUNT(*) FROM merchant_connection", nativeQuery = true)
    int countAllMerchantConnection();
}
