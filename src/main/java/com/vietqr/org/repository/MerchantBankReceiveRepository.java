package com.vietqr.org.repository;

import com.vietqr.org.dto.MerchantBankV2DTO;
import com.vietqr.org.entity.MerchantBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantBankReceiveRepository extends JpaRepository<MerchantBankReceiveEntity, Long> {
    @Query(value = "SELECT bank_id FROM merchant_bank_receive "
            + "WHERE bank_id = :id AND merchant_id != :merchantId LIMIT 1", nativeQuery = true)
    String checkExistedBankAccountInOtherMerchant(String id, String merchantId);

    @Query(value = "SELECT * FROM merchant_bank_receive "
            + "WHERE merchant_id = :merchantId AND bank_id = :id LIMIT 1", nativeQuery = true)
    MerchantBankReceiveEntity getMerchantBankReceiveByMerchantAndBankId(String merchantId, String id);

    @Query(value = "SELECT bank_id FROM merchant_bank_receive "
            + "WHERE merchant_id = :merchantId LIMIT 1", nativeQuery = true)
    String getBankIdReceiveByMerchant(String merchantId);

    @Query(value = "SELECT * FROM merchant_bank_receive "
            + "WHERE merchant_id = :merchantId AND bank_id = :bankId LIMIT 1 ", nativeQuery = true)
    MerchantBankReceiveEntity getMerchantBankByMerchantId(String merchantId, String bankId);

    @Query(value = "SELECT b.id AS merchantId, b.name AS merchantName "
            + "FROM merchant_bank_receive a "
            + "INNER JOIN merchant b ON a.merchant_id = b.id "
            + "INNER JOIN merchant_member c ON b.id = c.merchant_id "
            + "WHERE a.bank_id = :bankId AND c.user_id = :userId "
            + "ORDER BY b.name "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<MerchantBankV2DTO> getMerchantBankV2ByBankId(String bankId, String userId,
                                                      int offset, int size);

    @Query(value = "SELECT COUNT(b.id) "
            + "FROM merchant_bank_receive a "
            + "INNER JOIN merchant b ON a.merchant_id = b.id "
            + "INNER JOIN merchant_member c ON b.id = c.merchant_id "
            + "WHERE a.bank_id = :bankId AND c.user_id = :userId ", nativeQuery = true)
    int countMerchantBankV2ByBankId(String bankId, String userId);
}
