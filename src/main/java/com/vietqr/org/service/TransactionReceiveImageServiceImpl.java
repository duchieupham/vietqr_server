package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.repository.TransactionReceiveImageRepository;

import com.vietqr.org.entity.TransactionReceiveImageEntity;

@Service
public class TransactionReceiveImageServiceImpl implements TransactionReceiveImageService {

    @Autowired
    TransactionReceiveImageRepository repo;

    @Override
    public int insertTransactionReceiveImage(TransactionReceiveImageEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<String> getImgIdsByTransReceiveId(String transactionReceiveId) {
        return repo.getImgIdsByTransReceiveId(transactionReceiveId);
    }

    @Override
    public void removeTransactionImgage(String transactionId, String imgId) {
        repo.removeTransactionImgage(transactionId, imgId);
    }

}
