package com.vietqr.org.service;

import com.vietqr.org.dto.TransRequestDTO;
import com.vietqr.org.entity.TransReceiveRequestMappingEntity;
import com.vietqr.org.repository.TransReceiveRequestMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransReceiveRequestMappingServiceImpl implements TransReceiveRequestMappingService {
    @Autowired
    private TransReceiveRequestMappingRepository repo;

    @Override
    public int insert(TransReceiveRequestMappingEntity entity) {
        return repo.save(entity) != null ? 1 : 0;
    }

    @Override
    public int insertAll(List<TransReceiveRequestMappingEntity> entities) {
        return repo.saveAll(entities) != null ? 1 : 0;
    }

    @Override
    public TransReceiveRequestMappingEntity findById(String requestId) {
        return repo.findByRequestId(requestId);
    }

    @Override
    public List<TransRequestDTO> getTransactionReceiveRequest(List<String> listTransId) {
        return repo.getTransactionReceiveRequest(listTransId);
    }
}
