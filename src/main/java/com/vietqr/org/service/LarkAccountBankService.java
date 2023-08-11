package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.LarkBankDTO;
import com.vietqr.org.entity.LarkAccountBankEntity;

@Service
public interface LarkAccountBankService {

    public int insert(LarkAccountBankEntity entity);

    public List<LarkBankDTO> getLarkAccBanksByLarkId(String larkId);

    public void removeLarkAccBankByLarkId(String larkId);

    // for case send msg
    public List<String> getWebhooksByBankId(String bankId);
}
