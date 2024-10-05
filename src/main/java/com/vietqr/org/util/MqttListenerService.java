package com.vietqr.org.util;

import com.vietqr.org.mqtt.TidInternalSubscriber;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
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
        mqttClient.subscribe("#", 2);
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
        GoogleChatUtil googleChatUtil = new GoogleChatUtil();
        while (!reconnected) {
            attempt++;
            try {
                TimeUnit.SECONDS.sleep(Math.min(attempt * 2, 60)); // Increase wait time between retries, max 60 seconds
                mqttClient.reconnect();
                if (mqttClient.isConnected()) {
                    reconnected = true; // If reconnect succeeds, set reconnected to true
                }
                logger.info("MQTT Listener: Successfully reconnected after " + attempt + " attempt(s).");

            } catch (MqttException | InterruptedException e) {
                logger.error("MQTT Listener: ERROR: Reconnection attempt " + attempt + " failed: " + e.getMessage());
                if (mqttClient.isConnected()) {
                    reconnected = true; // If reconnect succeeds, set reconnected to true
                }
            } finally {
                if (mqttClient.isConnected()) {
                    reconnected = true;
                }
                if (EnvironmentUtil.isProduction()) {
                    String content = "MQTT CONNECTION LOST: " +
                            "\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F\uD83D\uDE4B\u200D♂\uFE0F." +
                            "\n\nMQTT Listener: ERROR: connectionLost: " + cause.getMessage() +
                            "\nTRYING RECONNECTION...\n";
                    googleChatUtil.sendMessageToGoogleChatInternal(content);
                }
            }
        }
        if (EnvironmentUtil.isProduction()) {
            String content = "MQTT Listener: Successfully reconnected after " + attempt + " attempt(s).";
            googleChatUtil.sendMessageToGoogleChatInternal(content);
        }

        try {
            if (mqttClient.isConnected()) {
                startListening();
            }
        } catch (MqttException e) {
            logger.error("startListening ERROR: " + e.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        MqttTopicHandlerScanner.MethodHandlerPair handlerPair = findHandlerForTopic(topic);
        try {
//            String existKey = idempotencyService.getResponseForKey("idempotency-lock:MQTT-KEY:" + message.getId()).orElse("");
            if (handlerPair != null
            ) {
//                if (idempotencyService.saveResponseForKey("MQTT-KEY:" + message.getId(), "", 30)) {
                logger.info("MQTT Listener: messageArrived HANDLER: " + topic + " message: " + new String(message.getPayload()) + "clientId: "
                + message.getId() + " at: " + System.currentTimeMillis());
                handlerPair.getMethod().invoke(handlerPair.getBean(), topic, message);
//                    idempotencyService.deleteResponseForKey("MQTT-KEY:" + message.getId());
//                } else {
//                    logger.warn("MQTT Listener: messageArrived NOT HANDLER OR ALREADY HANDLE: " + topic + " message: " + new String(message.getPayload()));
//                }
            } else {
                logger.warn("MQTT Listener: messageArrived NOT HANDLER OR ALREADY HANDLE: " + topic + " message: " + new String(message.getPayload()));
            }
        } catch (Exception e) {
            logger.error("MQTT Listener: messageArrived: " + topic + " cause: " + e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            logger.info("deliveryComplete: message: " + token.getMessage() + " client ID: " + token.getClient());
        } catch (MqttException e) {
            logger.info("deliveryComplete: ERROR: " + e.getMessage());
        }
    }

    private Map<String, MqttTopicHandlerScanner.MethodHandlerPair> initTopicHandlers() {
        return mqttTopicHandlerScanner.scanForMqttTopicHandlers(TidInternalSubscriber.class);
    }

    public void publishMessageToCommonTopic(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(1);
        mqttClient.publish(topic, message);
        logger.info("MQTT SendMessage: publishMessageToCommonTopic HANDLER: " + topic + " message: " + new String(message.getPayload()) + "clientId: "
                + message.getId() + " at: " + System.currentTimeMillis());
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
