package com.vietqr.org.service.social;

import com.vietqr.org.dto.SlackBankDTO;
import com.vietqr.org.entity.SlackAccountBankEntity;

import java.util.List;

public interface SlackAccountBankService {
    int insert(SlackAccountBankEntity entity);

    String checkExistedBankId(String bankId, String slackId);

    void deleteByBankIdAndSlackId(String bankId, String slackId);

    void deleteBySlackId(String slackId);

    List<SlackBankDTO> getSlackAccountBanks(String slackId);

    List<String> getWebhooksByBankId(String bankId);

    void updateWebHookSlack(String webhook, String slackId);
}
