package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQTTUtil {
    private static final Logger logger = Logger.getLogger(MQTTUtil.class);

    private static MqttListenerService mqttHandler;

    @Autowired
    public MQTTUtil(MqttListenerService mqttHandler) {
        MQTTUtil.mqttHandler = mqttHandler;
    }

    public static void sendMessage(String topic, String messageContent) {
        try {
            mqttHandler.publishMessageToCommonTopic(topic, messageContent);
        } catch (Exception e) {
            logger.error("MQTTUtil: ERROR: " + e.getMessage() + " at: " + System.currentTimeMillis());
        }
    }
}
