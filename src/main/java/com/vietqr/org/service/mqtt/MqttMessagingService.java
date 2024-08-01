package com.vietqr.org.service.mqtt;

import org.springframework.stereotype.Service;

@Service
public interface MqttMessagingService {
    public void sendMessageToBoxId(String boxId, String message);
}
