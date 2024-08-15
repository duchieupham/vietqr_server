package com.vietqr.org.service.mqtt;

import com.google.gson.Gson;
import com.vietqr.org.dto.VietQRCreateCustomerDTO;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class VietQRClient {
    private static final String BROKER = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "VietQRClient";
    private static final String REQUEST_TOPIC = "vietqr/request";
    private static final String RESPONSE_TOPIC = "vietqr/response";
    private static final String USERNAME = "VietQR123"; // thêm username
    private static final String PASSWORD = "VietQR123";
    private MqttClient client;

    public VietQRClient() throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(BROKER, CLIENT_ID, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(USERNAME);
        connOpts.setPassword(PASSWORD.toCharArray());
        client.connect(connOpts);

        client.subscribe(RESPONSE_TOPIC, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String response = new String(message.getPayload());
                System.out.println("Received response: " + response);
                // Xử lý phản hồi từ server
            }
        });
    }

    public void sendRequest(VietQRCreateCustomerDTO dto) throws MqttException {
        Gson gson = new Gson();
        String payload = gson.toJson(dto);
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(2);
        client.publish(REQUEST_TOPIC, message);
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

//    public static void main(String[] args) {
//        try {
//            VietQRClient client = new VietQRClient();
//            VietQRCreateCustomerDTO dto = new VietQRCreateCustomerDTO(10000L, "Dondathangsanpham", "0373568944", "MB",
//                    "NGUYEN PHUONG NHAI LINH", "5035Linh9", null, "123", null);
//            client.sendRequest(dto);
//            // Giữ kết nối hoặc đợi phản hồi tùy theo yêu cầu cụ thể
//            Thread.sleep(10000); // Giữ chương trình chạy 10 giây để nhận phản hồi
//            client.disconnect();
//        } catch (MqttException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
