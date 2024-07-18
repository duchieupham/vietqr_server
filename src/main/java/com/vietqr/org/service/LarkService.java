package com.vietqr.org.service;

import java.util.List;

import com.vietqr.org.dto.LarkInfoDetailDTO;
import org.springframework.stereotype.Service;

import com.vietqr.org.entity.LarkEntity;

@Service
public interface LarkService {

    public int insertLark(LarkEntity entity);

    public List<LarkEntity> getLarksByUserId(String userId);
    LarkEntity getLarkByUserId(String userId);

    public LarkEntity getLarkById(String id);
    LarkEntity getLarkByWebhook(String webhook);

    public void removeLarkById(String id);

    void updateLark(LarkEntity larkEntity);

    void updateLarkWebhook(String larkId, String webhook);
    int countLarksByUserId(String userId);
    List<LarkInfoDetailDTO> getLarksByUserIdWithPagination(String userId, int offset, int size);
}
