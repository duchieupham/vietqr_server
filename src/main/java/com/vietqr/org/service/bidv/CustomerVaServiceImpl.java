package com.vietqr.org.service.bidv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.bidv.CustomerVaEntity;
import com.vietqr.org.repository.CustomerVaRepository;

@Service
public class CustomerVaServiceImpl implements CustomerVaService {

    @Autowired
    CustomerVaRepository repo;

    @Override
    public int insert(CustomerVaEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

}
