package com.vietqr.org.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.vietqr.org.entity.TransactionReceiveImageEntity;

@Service
public interface TransactionReceiveImageService {
    public int insertTransactionReceiveImage(TransactionReceiveImageEntity entity);

    public List<String> getImgIdsByTransReceiveId(String transactionReceiveId);

    void removeTransactionImgage(String transactionId, String imgId);
}
