package com.vietqr.org.service;

import org.springframework.stereotype.Service;
import com.vietqr.org.entity.AccountShareEntity;

@Service
public interface AccountShareService {

    public int insertAccountShare(AccountShareEntity entity);
}
