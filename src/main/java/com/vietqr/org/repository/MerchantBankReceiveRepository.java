package com.vietqr.org.repository;

import com.vietqr.org.entity.MerchantBankReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantBankReceiveRepository extends JpaRepository<MerchantBankReceiveEntity, Long> {
    @Query(value = "SELECT bank_id FROM merchant_bank_receive "
            + "WHERE bank_id = :id AND merchant_id != :merchantId LIMIT 1", nativeQuery = true)
    String checkExistedBankAccountInOtherMerchant(String id, String merchantId);

    @Query(value = "SELECT * FROM merchant_bank_receive "
            + "WHERE merchant_id = :merchantId AND bank_id = :id LIMIT 1", nativeQuery = true)
    MerchantBankReceiveEntity getMerchantBankReceiveByMerchantAndBankId(String merchantId, String id);
}
