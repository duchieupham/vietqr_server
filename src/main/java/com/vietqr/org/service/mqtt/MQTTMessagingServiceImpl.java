package com.vietqr.org.service.mqtt;

import com.vietqr.org.util.MQTTUtil;
import org.springframework.stereotype.Service;

@Service
public class MQTTMessagingServiceImpl implements MQTTMessagingService {
    @Override
    public void sendMessageToBoxId(String boxId, String message) {
        try {
            String topicQrBox = "vietqr/boxId/" + boxId;
            MQTTUtil.sendMessage(topicQrBox, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
