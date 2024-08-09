//package com.vietqr.org.util;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MQTTUtil {
//
//    private static MQTTHandler mqttHandler;
//
//    @Autowired
//    public MQTTUtil(MQTTHandler mqttHandler) {
//        MQTTUtil.mqttHandler = mqttHandler;
//    }
//
//    public static void sendMessage(String topic, String messageContent) {
//        mqttHandler.sendMessage(topic, messageContent);
//    }
//}
