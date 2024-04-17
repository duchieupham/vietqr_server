package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.LarkBankDTO;
import com.vietqr.org.entity.LarkAccountBankEntity;
import com.vietqr.org.repository.LarkAccountBankRepository;

@Service
public class LarkAccountBankServiceImpl implements LarkAccountBankService {

    @Autowired
    LarkAccountBankRepository repo;

    @Override
    public int insert(LarkAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public List<LarkBankDTO> getLarkAccBanksByLarkId(String larkId) {
        return repo.getLarkAccBanksByLarkId(larkId);
    }

    @Override
    public void removeLarkAccBankByLarkId(String larkId) {
        repo.removeLarkAccBankByLarkId(larkId);
    }

    @Override
    public List<String> getWebhooksByBankId(String bankId) {
        return repo.getWebhooksByBankId(bankId);
    }

    @Override
    public void removeLarkAccBankByLarkIdAndBankId(String larkId, String bankId) {
        repo.removeLarkAccBankByLarkIdAndBankId(larkId, bankId);
    }

}
