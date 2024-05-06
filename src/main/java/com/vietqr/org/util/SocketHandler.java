package com.vietqr.org.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private static final Logger logger = Logger.getLogger(SocketHandler.class);

    private List<WebSocketSession> notificationSessions = new ArrayList<>();
    private List<WebSocketSession> loginSessions = new ArrayList<>();
    private List<WebSocketSession> ecLoginSessions = new ArrayList<>();
    private List<WebSocketSession> transactionSessions = new ArrayList<>();
    private List<WebSocketSession> notificationBoxSessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            logger.info("WS: add session: " + session.toString());
            String userId = (String) session.getAttributes().get("userId");
            String loginId = (String) session.getAttributes().get("loginId");
            String ecLoginId = (String) session.getAttributes().get("ecLoginId");
            String transactionRefId = (String) session.getAttributes().get("refId");
            String boxId = (String) session.getAttributes().get("boxId");

            if (userId != null && !userId.trim().isEmpty()) {
                // save userId for this session
                session.getAttributes().put("userId", userId);
                notificationSessions.add(session);
                logger.info("WS: userSessions size: " + notificationSessions.size());
            } else if (loginId != null && !loginId.trim().isEmpty()) {
                // save loginId for this session
                session.getAttributes().put("loginId", loginId);
                loginSessions.add(session);
            } else if (ecLoginId != null && !ecLoginId.trim().isEmpty()) {
                // save loginId for this session
                session.getAttributes().put("ecLoginId", ecLoginId);
                ecLoginSessions.add(session);
            } else if (transactionRefId != null && !transactionRefId.trim().isEmpty()) {
                // save transactionRefId for this session
                session.getAttributes().put("refId", transactionRefId);
                transactionSessions.add(session);
            } else if (boxId != null && !boxId.trim().isEmpty()) {
                // save transactionRefId for this session
                session.getAttributes().put("boxId", boxId);
                notificationBoxSessions.add(session);
                Map<String, String> data = new HashMap<>();
                data.put("status", "SUCCESS");
                data.put("notificationType", NotificationUtil.getNotiTypeConnectSuccess());
                sendMessageToBoxId(boxId, data);
            } else {
                logger.error("WS: userId is missing");
                session.close();
            }
        } catch (Exception e) {
            logger.error("WS: error add session: " + e.toString());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        logger.info("WS: handleTextMessage: " + session.getId() + " - " + message.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        notificationSessions.remove(session);
        loginSessions.remove(session);
        transactionSessions.remove(session);
        notificationBoxSessions.remove(session);
        //
        logger.info("WS: remove session: " + session.toString());
        logger.info("WS: notificationSessions size: " + notificationSessions.size());
        logger.info("WS: loginSessions size: " + loginSessions.size());
        logger.info("WS: ecLoginSessions size: " + ecLoginSessions.size());
        logger.info("WS: transactionSessions size: " + transactionSessions.size());
        //
    }

    public void sendMessageEcLoginToWeb(String ecLoginId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageEcLoginToWeb");
        logger.info("WS: ecLoginSessions: " + ecLoginSessions.size());
        for (WebSocketSession session : ecLoginSessions) {
            logger.info("WS: ec-login session ID: " + session.getId());
            logger.info("WS: ec-login session Attributes: " + session.getAttributes());
            Object sessionEcLoginId = session.getAttributes().get("ecLoginId");
            if (sessionEcLoginId != null && sessionEcLoginId.equals(ecLoginId)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public void sendMessageLoginToWeb(String loginId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageLoginToWeb");
        logger.info("WS: loginSessions: " + loginSessions.size());
        for (WebSocketSession session : loginSessions) {
            logger.info("WS: login session ID: " + session.getId());
            logger.info("WS: login session Attributes: " + session.getAttributes());
            Object sessionLoginId = session.getAttributes().get("loginId");
            if (sessionLoginId != null && sessionLoginId.equals(loginId)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public void sendMessageToUser(String userId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageToUser");
        logger.info("WS: notificationSessions: " + notificationSessions.size());
        for (WebSocketSession session : notificationSessions) {
            logger.info("WS: session ID: " + session.getId());
            logger.info("WS: session Attributes: " + session.getAttributes());
            Object sessionUserId = session.getAttributes().get("userId");
            if (sessionUserId != null && sessionUserId.equals(userId)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public void sendMessageToBoxId(String boxId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageToUser");
        logger.info("WS: notificationSessions: " + notificationBoxSessions.size());
        for (WebSocketSession session : notificationBoxSessions) {
            logger.info("WS: session ID: " + session.getId());
            logger.info("WS: session Attributes: " + session.getAttributes());
            Object sessionUserId = session.getAttributes().get("boxId");
            if (sessionUserId != null && sessionUserId.equals(boxId)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    public void sendMessageToTransactionRefId(String refId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageToTransactionRefId");
        logger.info("WS: transactionSessions: " + transactionSessions.size());
        for (WebSocketSession session : transactionSessions) {
            logger.info("WS: session ID: " + session.getId());
            logger.info("WS: session Attributes: " + session.getAttributes());
            Object sessionUserId = session.getAttributes().get("refId");
            if (sessionUserId != null && sessionUserId.equals(refId)) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }
}
