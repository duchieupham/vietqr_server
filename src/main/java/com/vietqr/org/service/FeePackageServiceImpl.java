package com.vietqr.org.service;

import com.vietqr.org.repository.FeePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeePackageServiceImpl implements FeePackageService {

    @Autowired
    private FeePackageRepository repo;
}
