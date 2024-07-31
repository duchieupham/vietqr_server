package com.vietqr.org.util;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQTTHandler {

    private static final Logger logger = Logger.getLogger(MQTTHandler.class);

    @Value("${spring.mqtt.broker}")
    private static final String BROKER_URL = "tcp://112.78.1.220:1883";
    private static final String CLIENT_ID = "VietQRClient";

    @Value("${spring.mqtt.username}")
    private static final String USERNAME = "vietqrbnsmqtt";

    @Value("${spring.mqtt.password}")
    private static final String PASSWORD = "123456789";
    private MqttClient client;

    @Bean
    public IMqttClient mqttClient() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(BROKER_URL, CLIENT_ID, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        client.connect(connOpts);

        // Set the callback for receiving messages
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                logger.error("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                logger.info("Message arrived. Topic: " + topic + " Message: " + new String(message.getPayload()));
                // Handle the incoming message here
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Not used for receiving messages
            }
        });
        return client;
    }

    public void sendMessageToQrBox(String boxId, String messageContent) {
        try {
            MqttMessage message = new MqttMessage(messageContent.getBytes());
            message.setQos(1); // QoS level 1
            String TOPIC_BOX_ID = "vietqr/boxId/%";
            client.publish(String.format(TOPIC_BOX_ID, boxId), message);
        } catch (Exception e) {
            logger.error("send Message Failed: ERROR: " + e.getMessage() + " boxId: " + boxId +
                    "message: " + messageContent);
        }
    }

    public void subscribeToTopic(String topic) {
        try {
            client.subscribe(topic, 1); // QoS level 1
            logger.info("Subscribed to topic: " + topic);
        } catch (MqttException e) {
            logger.error("Subscription failed: ERROR: " + e.getMessage() + " Topic: " + topic);
        }
    }
}
