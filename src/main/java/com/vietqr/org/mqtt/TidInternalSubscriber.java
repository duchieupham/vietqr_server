package com.vietqr.org.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.HandleSyncBoxQrDTO;
import com.vietqr.org.dto.SyncBoxQrDTO;
import com.vietqr.org.entity.QrBoxSyncEntity;
import com.vietqr.org.service.QrBoxSyncService;
import com.vietqr.org.util.*;
import com.vietqr.org.util.annotation.MqttTopicHandler;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.UUID;

@Component
public class TidInternalSubscriber {

    private static final Logger logger = Logger.getLogger(TidInternalSubscriber.class);

    private static final int CODE_LENGTH = 6;
    private static final String NUMBERS = "0123456789";

    @Autowired
    private QrBoxSyncService qrBoxSyncService;

    @Autowired
    private MqttListenerService mqttListenerService;

    @PostConstruct
    public void init() {
        System.out.println("TidInternalSubscriber initialized");
        System.out.println("qrBoxSyncService: " + qrBoxSyncService);
        System.out.println("mqttListenerService: " + mqttListenerService);
    }

    @MqttTopicHandler(topic = "/vqr/handle-box")
    public void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HandleSyncBoxQrDTO dto = mapper.readValue(message.getPayload(), HandleSyncBoxQrDTO.class);
            String checkSum = BoxTerminalRefIdUtil.encryptMacAddr(dto.getMacAddr());
            if (checkSum.equals(dto.getCheckSum())) {
                String macAddr = dto.getMacAddr().replaceAll("\\:", "");
                dto.setMacAddr(macAddr);
                macAddr = dto.getMacAddr().replaceAll("\\.", "");
                String qrBoxCode = getRandomNumberUniqueQRBox();
                String certificate = EnvironmentUtil.getVietQrBoxInteralPrefix() + BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode + macAddr);
                String boxId = BoxTerminalRefIdUtil.encryptQrBoxId(qrBoxCode);
                QrBoxSyncEntity entity = qrBoxSyncService.getByMacAddress(macAddr);
                if (entity != null) {
                    boxId = BoxTerminalRefIdUtil.encryptQrBoxId(entity.getQrBoxCode());
                } else {
                    entity = new QrBoxSyncEntity();
                    entity.setId(UUID.randomUUID().toString());
                    entity.setTimeCreated(DateTimeUtil.getCurrentDateTimeUTC());
                    entity.setTimeSync(0);
                    entity.setQrBoxCode(qrBoxCode);
                    entity.setCertificate(certificate);
                    entity.setMacAddress(macAddr);
                    entity.setIsActive(false);
                    entity.setQrName("");
                    entity.setLastChecked(0);
                    entity.setStatus(0);
                }
                qrBoxSyncService.insert(entity);

                //send to macAddress
                SyncBoxQrDTO syncBoxQrDTO = new SyncBoxQrDTO(certificate, boxId);
                mqttListenerService.publishMessageToCommonTopic("/vqr/handle-box/response/" + dto.getMacAddr(),
                        mapper.writeValueAsString(syncBoxQrDTO));
            }
        } catch (Exception e) {
            logger.error("TidInternalSubscriber: ERROR: " + e.getMessage() +
                    " at: " + System.currentTimeMillis());
        }
    }

    private String getRandomNumberUniqueQRBox() {
        String result = "";
        String checkExistedCode = "";
        String code = "";
        try {
            do {
                code = "VVB" + getRawTerminalCode();
                checkExistedCode = qrBoxSyncService.checkExistQRBoxCode(code);
                if (checkExistedCode == null || checkExistedCode.trim().isEmpty()) {
                    checkExistedCode = qrBoxSyncService.checkExistQRBoxCode(code);
                }
            } while (!StringUtil.isNullOrEmpty(checkExistedCode));
            result = code;
        } catch (Exception ignored) {
        }
        return result;
    }

    private String getRawTerminalCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(NUMBERS.length());
            code.append(NUMBERS.charAt(randomIndex));
        }
        return code.toString();
    }
}
