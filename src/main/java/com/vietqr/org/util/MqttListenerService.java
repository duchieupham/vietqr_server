package com.vietqr.org.util;

import com.vietqr.org.mqtt.TidInternalSubscriber;
import com.vietqr.org.service.QrBoxSyncService;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqttListenerService implements MqttCallback {

    private static final Logger logger = Logger.getLogger(MqttListenerService.class);
    private static final int CODE_LENGTH = 6;
    private static final String NUMBERS = "0123456789";
    private final IMqttClient mqttClient;
    private final MqttTopicHandlerScanner mqttTopicHandlerScanner;
    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    private Map<String, MqttTopicHandlerScanner.MethodHandlerPair> topicHandlers = new HashMap<>();


    public MqttListenerService(IMqttClient mqttClient, MqttTopicHandlerScanner mqttTopicHandlerScanner) {
        this.mqttClient = mqttClient;
        this.mqttTopicHandlerScanner = mqttTopicHandlerScanner;
        this.topicHandlers = initTopicHandlers();
    }

    // Thay đổi phương thức isTopicMatching để xử lý wildcard # và +
    public static boolean isTopicMatching(String topicPattern, String actualTopic) {
        String regex = topicPattern
                .replace("+", "[^/]+")  // Thay thế + bằng regex cho một phần tử bất kỳ không có dấu "/"
                .replace("#", ".*");    // Thay thế # bằng regex cho phần còn lại của chuỗi
        return actualTopic.matches(regex);
    }

    @PostConstruct
    public void startListening() throws MqttException {
        mqttClient.setCallback(this);
        mqttClient.subscribe("#", 1);
    }

    @PreDestroy
    public void stopListening() throws MqttException {
        mqttClient.disconnect();
    }

    @Override
    public void connectionLost(Throwable cause) {
        // Handle connection lost
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message received from topic: " + topic);
        System.out.println("Message: " + new String(message.getPayload()));

        //MqttTopicHandlerScanner.MethodHandlerPair handlerPair = topicHandlers.get(topic);
        // Tìm kiếm chủ đề trong danh sách các handler
        MqttTopicHandlerScanner.MethodHandlerPair handlerPair = findHandlerForTopic(topic);
        try {
            if (handlerPair != null) {
                handlerPair.getMethod().invoke(handlerPair.getBean(), topic, message);
            } else {
                System.out.println("No handler for topic: " + topic);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
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

    // Tìm kiếm handler phù hợp với chủ đề được nhận
    private MqttTopicHandlerScanner.MethodHandlerPair findHandlerForTopic(String topic) {
        for (Map.Entry<String, MqttTopicHandlerScanner.MethodHandlerPair> entry : topicHandlers.entrySet()) {
            String key = entry.getKey();
            if (isTopicMatching(key, topic)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
