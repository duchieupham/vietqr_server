package com.vietqr.org.service;

import com.vietqr.org.repository.MerchantTransReceiveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantTransReceiveRequestServiceImpl implements MerchantTransReceiveRequestService {

    @Autowired
    private MerchantTransReceiveRequestRepository repo;
}
