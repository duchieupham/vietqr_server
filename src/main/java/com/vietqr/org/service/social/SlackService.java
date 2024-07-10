package com.vietqr.org.service.social;

import com.vietqr.org.dto.SlackInfoDetailDTO;
import com.vietqr.org.entity.SlackEntity;

import java.util.List;

public interface SlackService {
    void insert(SlackEntity entity);

    void updateSlack(String webhook, String slackId);

    SlackEntity getSlackById(String id);

    SlackEntity getSlackByUserId(String userId);

    void updateSlack(SlackEntity slackEntity);

    void removeSlack(String id);

    int countSlacksByUserId(String userId);

    List<SlackInfoDetailDTO> getSlacksByUserIdWithPagination(String userId, int offset, int size);

    SlackEntity getSlackByWebhook(String webhook);
}
