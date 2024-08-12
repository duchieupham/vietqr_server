package com.vietqr.org.util;

import com.vietqr.org.mqtt.TidInternalSubscriber;
import com.vietqr.org.service.QrBoxSyncService;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqttListenerService implements MqttCallback {

    private final IMqttClient mqttClient;

    private static final Logger logger = Logger.getLogger(MqttListenerService.class);
    private final MqttTopicHandlerScanner mqttTopicHandlerScanner;
    private static final int CODE_LENGTH = 6;
    private static final String NUMBERS = "0123456789";

    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    private Map<String, MqttTopicHandlerScanner.MethodHandlerPair> topicHandlers = new HashMap<>();


    public MqttListenerService(IMqttClient mqttClient, MqttTopicHandlerScanner mqttTopicHandlerScanner) {
        this.mqttClient = mqttClient;
        this.mqttTopicHandlerScanner = mqttTopicHandlerScanner;
        this.topicHandlers = initTopicHandlers();
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
        MqttTopicHandlerScanner.MethodHandlerPair handlerPair = topicHandlers.get(topic);
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

}
