package com.vietqr.org.service.social;

import com.vietqr.org.dto.SlackBankDTO;
import com.vietqr.org.entity.SlackAccountBankEntity;
import com.vietqr.org.repository.SlackAccountBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlackAccountBankServiceImpl implements SlackAccountBankService {

    @Autowired
    SlackAccountBankRepository repo;

    @Override
    public int insert(SlackAccountBankEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public String checkExistedBankId(String bankId, String slackId) {
        return repo.checkExistedBankId(bankId, slackId);
    }

    @Override
    public void deleteByBankIdAndSlackId(String bankId, String slackId) {
        repo.deleteByBankIdAndSlackId(bankId, slackId);
    }

    @Override
    public void deleteBySlackId(String slackId) {
        repo.deleteBySlackId(slackId);
    }

    @Override
    public List<SlackBankDTO> getSlackAccountBanks(String slackId) {
        return repo.getSlackAccountBanks(slackId);
    }

    @Override
    public List<String> getWebhooksByBankId(String bankId) {
        return repo.getWebhooksByBankId(bankId);
    }

    @Override
    public void updateWebHookSlack(String webhook, String slackId) {
        repo.updateWebHookSlack(webhook, slackId);
    }
}