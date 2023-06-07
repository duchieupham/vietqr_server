package com.vietqr.org.util;

import java.io.IOException;
import java.util.ArrayList;
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            logger.info("WS: add session: " + session.toString());
            String userId = (String) session.getAttributes().get("userId");
            String loginId = (String) session.getAttributes().get("loginId");

            if (userId != null && !userId.trim().isEmpty()) {
                // save userId for this session
                session.getAttributes().put("userId", userId);
                notificationSessions.add(session);
                logger.info("WS: userSessions size: " + notificationSessions.size());
            } else if (loginId != null && !loginId.trim().isEmpty()) {
                // save loginId for this session
                session.getAttributes().put("loginId", loginId);
                loginSessions.add(session);
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
        logger.info("WS: remove session: " + session.toString());
        logger.info("WS: notificationSessions size: " + notificationSessions.size());
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

}
