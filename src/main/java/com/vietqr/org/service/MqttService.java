//package com.vietqr.org.service;
//
//import org.eclipse.paho.client.mqttv3.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MqttService implements MqttCallback {
//
//    private final MqttClient mqttClient;
//
//    @Autowired
//    public MqttService(MqttClient mqttClient) {
//        this.mqttClient = mqttClient;
//        this.mqttClient.setCallback(this);
//    }
//
//    public void subscribe(String topic) throws MqttException{
//        mqttClient.subscribe(topic);
//    }
//
//
//    public void publish(String topic , String content) throws MqttException{
//        MqttMessage message = new MqttMessage(content.getBytes());
//        message.setQos(2);
//        mqttClient.publish(topic, message);
//    }
//
//    @Override
//    public void connectionLost(Throwable throwable) {
//        System.out.println("Connection lost: " + throwable.getMessage());
//    }
//
//    @Override
//    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
//        System.out.println("Message arrived. Topic: " + mqttMessage+ " Message: " + new String(mqttMessage.getPayload()));
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//        System.out.println("Delivery complete");
//    }
//}
