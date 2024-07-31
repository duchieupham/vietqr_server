package com.vietqr.org.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class MqttConfig {
//    @Bean
//    public MqttClient mqttClient() throws MqttException{
//        String broker ="tcp://broker.hivemq.com:1883";
//        String  clientId ="viet-qr-client";
//        MemoryPersistence persistence = new MemoryPersistence();
//
//        MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
//        MqttConnectOptions connectOpts = new MqttConnectOptions();
//        connectOpts.setCleanSession(true);
//        mqttClient.connect();
//
//        return mqttClient;
//
//    }




}
