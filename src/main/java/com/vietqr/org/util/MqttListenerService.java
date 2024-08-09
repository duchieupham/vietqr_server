package com.vietqr.org.util;

import com.vietqr.org.util.annotation.MqttTopicHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqttListenerService implements MqttCallback {

    private final IMqttClient mqttClient;

    private final Map<String, Method> topicHandlers = new HashMap<>();

    public MqttListenerService(IMqttClient mqttClient) {
        this.mqttClient = mqttClient;
        initTopicHandlers();
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

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Handle delivery completion
    }

    private void initTopicHandlers() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(MqttTopicHandler.class)) {
                MqttTopicHandler annotation = method.getAnnotation(MqttTopicHandler.class);
                topicHandlers.put(annotation.topic(), method);
            }
        }
    }

    private void publishMessageToCommonTopic(String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(1);
        mqttClient.publish(topic, message);
    }

    @MqttTopicHandler(topic = "your/topic1")
    public void handleTopic1Message(String topic, MqttMessage message) {
        System.out.println("Handling message from topic1: " + new String(message.getPayload()));
        // Add your logic here
    }
}
