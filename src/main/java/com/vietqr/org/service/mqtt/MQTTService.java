//package com.vietqr.org.service.mqtt;
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
//public class MQTTService {
//
//    private static final Logger logger = Logger.getLogger(MQTTService.class);
//
//    @Autowired
//    private IMqttClient mqttClient;
//
//    @Autowired
//    private SubscribeToTopicAspect subscribeToTopicAspect;
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
//                logger.info("Message arrived. Topic: " + topic + " Message: " + new String(message.getPayload()));
//                subscribeToTopicAspect.handleMessage(topic, new String(message.getPayload()));
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {}
//        });
//    }
//
//    public void sendMessageToQrBox(String boxId, String messageContent) {
//        try {
//            MqttMessage message = new MqttMessage(messageContent.getBytes());
//            message.setQos(1);
//            mqttClient.publish("vietQr-box/boxId/" + boxId, message);
//        } catch (Exception e) {
//            logger.error("Send message failed: " + e.getMessage());
//        }
//    }
//
//    public void subscribeToTopic(String topic) {
//        try {
//            mqttClient.subscribe(topic, 1);
//            logger.info("Subscribed to topic: " + topic);
//        } catch (MqttException e) {
//            logger.error("Subscription failed: " + e.getMessage());
//        }
//    }
//}
