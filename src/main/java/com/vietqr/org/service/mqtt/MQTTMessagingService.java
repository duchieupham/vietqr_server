package com.vietqr.org.service.mqtt;

import org.springframework.stereotype.Service;

@Service
public interface MQTTMessagingService {
    public void sendMessageToBoxId(String boxId, String message);
}
