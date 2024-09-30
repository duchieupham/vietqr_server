package com.vietqr.org.util;

import com.vietqr.org.mqtt.TidInternalSubscriber;

import com.vietqr.org.service.redis.IdempotencyService;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MqttListenerService implements MqttCallback {

    private static final Logger logger = Logger.getLogger(MqttListenerService.class);
    private final IMqttClient mqttClient;
    private final MqttTopicHandlerScanner mqttTopicHandlerScanner;

    private Map<String, MqttTopicHandlerScanner.MethodHandlerPair> topicHandlers = new HashMap<>();

    @Autowired
    private IdempotencyService idempotencyService;
    public MqttListenerService(IMqttClient mqttClient, MqttTopicHandlerScanner mqttTopicHandlerScanner) {
        this.mqttClient = mqttClient;
        this.mqttTopicHandlerScanner = mqttTopicHandlerScanner;
        this.topicHandlers = initTopicHandlers();
    }

    // Phương thức cải tiến để khớp wildcard # và +
    public static boolean isTopicMatching(String topicPattern, String actualTopic) {
        String[] patternLevels = topicPattern.split("/");
        String[] topicLevels = actualTopic.split("/");

        for (int i = 0; i < patternLevels.length; i++) {
            // Nếu vượt quá độ dài của actualTopic, chủ đề không khớp
            if (i >= topicLevels.length) {
                return false;
            }

            // Nếu gặp wildcard #, khớp toàn bộ phần còn lại
            if (patternLevels[i].equals("#")) {
                return true;  // Khớp toàn bộ phần còn lại của chuỗi actualTopic
            }

            // Nếu gặp wildcard +, khớp với đúng một cấp của actualTopic
            if (!patternLevels[i].equals("+") && !patternLevels[i].equals(topicLevels[i])) {
                return false;  // Nếu không khớp, trả về false
            }
        }

        // Chủ đề khớp nếu có cùng độ dài với topicPattern
        return patternLevels.length == topicLevels.length;
    }

    @PostConstruct
    public void startListening() throws MqttException {
        mqttClient.setCallback(this);
        mqttClient.subscribe("#", 1);
    }

    @PreDestroy
    public void stopListening() throws MqttException {
        logger.error("MQTT Listener: ERROR: STOP LISTENING");
        mqttClient.disconnect();
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Handle connection lost
        logger.error("MQTT Listener: ERROR: connectionLost: " + cause.getMessage());
        boolean reconnected = false;
        int attempt = 0;
        while (!reconnected) {
            attempt++;
            try {
                TimeUnit.SECONDS.sleep(Math.min(attempt * 5, 60)); // Increase wait time between retries, max 60 seconds
                mqttClient.reconnect();
                reconnected = true; // If reconnect succeeds, set reconnected to true
                logger.info("MQTT Listener: Successfully reconnected after " + attempt + " attempt(s).");

            } catch (MqttException | InterruptedException e) {
                logger.error("MQTT Listener: ERROR: Reconnection attempt " + attempt + " failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        MqttTopicHandlerScanner.MethodHandlerPair handlerPair = findHandlerForTopic(topic);
        try {
            String existKey = idempotencyService.getResponseForKey("MQTT-KEY:" + message.getId()).orElse("");
            if (handlerPair != null && StringUtil.isNullOrEmpty(existKey) &&
                    idempotencyService.saveResponseForKey("MQTT-KEY:" + message.getId(), "", 30)) {
                logger.info("MQTT Listener: messageArrived HANDLER: " + topic + " message: " + new String(message.getPayload()));
                handlerPair.getMethod().invoke(handlerPair.getBean(), topic, message);
            } else {
                logger.warn("MQTT Listener: messageArrived NOT HANDLER OR ALREADY HANDLE: " + topic + " message: " + new String(message.getPayload()));
            }
        } catch (Exception e) {
            logger.error("MQTT Listener: messageArrived: " + topic + " cause: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Handle delivery completion
    }

    private Map<String, MqttTopicHandlerScanner.MethodHandlerPair> initTopicHandlers() {
        return mqttTopicHandlerScanner.scanForMqttTopicHandlers(TidInternalSubscriber.class);
    }

    public void publishMessageToCommonTopic(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(1);
        mqttClient.publish(topic, message);
    }

    // Phương thức tìm kiếm handler phù hợp
//    private MqttTopicHandlerScanner.MethodHandlerPair findHandlerForTopic(String topic) {
//        for (Map.Entry<String, MqttTopicHandlerScanner.MethodHandlerPair> entry : topicHandlers.entrySet()) {
//            String key = entry.getKey();
//            // Sử dụng phương thức isTopicMatching để tìm chủ đề phù hợp
//            if (isTopicMatching(key, topic)) {
//                return entry.getValue();
//            }
//        }
//        return null;
//    }
    // Phương thức tìm kiếm handler phù hợp
    private MqttTopicHandlerScanner.MethodHandlerPair findHandlerForTopic(String topic) {
        MqttTopicHandlerScanner.MethodHandlerPair matchedHandler = null;

        for (Map.Entry<String, MqttTopicHandlerScanner.MethodHandlerPair> entry : topicHandlers.entrySet()) {
            String key = entry.getKey();
            // Sử dụng phương thức isTopicMatching để tìm chủ đề phù hợp
            if (isTopicMatching(key, topic)) {
                matchedHandler = entry.getValue();
                break;
            }
        }
        return matchedHandler;
    }


}
