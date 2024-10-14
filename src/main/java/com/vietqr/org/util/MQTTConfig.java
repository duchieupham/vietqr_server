package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MQTTConfig {

    private static final Logger logger = Logger.getLogger(MQTTConfig.class);

    @Value("${spring.mqtt.broker}")
    private String brokerUrl;

    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Bean
    public IMqttClient mqttClient() throws MqttException {
        String clientId = UUID.randomUUID().toString();
        logger.info("MQTT CONFIG clientID: " + clientId);
        MemoryPersistence persistence = new MemoryPersistence();
        IMqttClient client = new MqttClient(brokerUrl, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        connOpts.setAutomaticReconnect(true);
        client.connect(connOpts);
        return client;
    }
}
