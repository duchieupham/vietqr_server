package com.vietqr.org.service.redis;

import com.vietqr.org.entity.redis.RegisterBankEntity;
import com.vietqr.org.repository.redis.RegisterBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisterBankServiceImpl implements RegisterBankService {

    @Autowired
    private RegisterBankRepository repo;

    @Override
    public void removeAll() {
        repo.deleteAll();
    }

    @Override
    public void saveAll(List<RegisterBankEntity> list) {
        repo.saveAll(list);
    }
}
