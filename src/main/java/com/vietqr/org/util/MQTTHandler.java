//package com.vietqr.org.util;
//
//import com.vietqr.org.util.annotation.SubscribeToTopicAspect;
//import org.apache.log4j.Logger;
//import org.eclipse.paho.client.mqttv3.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//@Service
//public class MQTTHandler {
//
//    private static final Logger logger = Logger.getLogger(MQTTHandler.class);
//
//    private final IMqttClient mqttClient;
//    private final SubscribeToTopicAspect subscribeToTopicAspect;
//
//    @Autowired
//    public MQTTHandler(IMqttClient mqttClient, SubscribeToTopicAspect subscribeToTopicAspect) {
//        this.mqttClient = mqttClient;
//        this.subscribeToTopicAspect = subscribeToTopicAspect;
//    }
//
//    @PostConstruct
//    public void init() {
//        mqttClient.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable cause) {
//                logger.error("Connection lost: " + cause.getMessage());
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                String payload = new String(message.getPayload());
//                logger.info("Message arrived. Topic: " + topic + " Message: " + payload);
//                subscribeToTopicAspect.handleMessage(topic, payload);
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                // Not used for receiving messages
//            }
//        });
//    }
//
//    public void sendMessage(String topic, String messageContent) {
//        try {
//            MqttMessage message = new MqttMessage(messageContent.getBytes());
//            message.setQos(1);
//            mqttClient.publish(topic, message);
//        } catch (Exception e) {
//            logger.error("send Message Failed: ERROR: " + e.getMessage() + " topic: " + topic +
//                    " message: " + messageContent);
//        }
//    }
//
//    public void subscribeToAllTopics() {
//        try {
//            mqttClient.subscribe("#", 1); // Subscribe to all topics using wildcard #
//            logger.info("Subscribed to all topics");
//        } catch (MqttException e) {
//            logger.error("Subscription failed: ERROR: " + e.getMessage());
//        }
//    }
//}
