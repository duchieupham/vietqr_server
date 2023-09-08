package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.LarkEntity;
import com.vietqr.org.repository.LarkRepository;

@Service
public class LarkServiceImpl implements LarkService {

    @Autowired
    LarkRepository repo;

    @Override
    public int insertLark(LarkEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<LarkEntity> getLarksByUserId(String userId) {
        return repo.getLarksByUserId(userId);
    }

    @Override
    public void removeLarkById(String id) {
        repo.removeLarkById(id);
    }

    @Override
    public LarkEntity getLarkById(String id) {
        return repo.getLarkById(id);
    }

}
