package com.vietqr.org.service;
import com.vietqr.org.entity.TransactionBidvEntity;
import org.springframework.stereotype.Service;

@Service
public interface TransactionBidvService {

    void insert(TransactionBidvEntity entity);
}
