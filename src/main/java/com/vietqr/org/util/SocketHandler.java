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

    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            logger.info("WS: add session: " + session.toString());
            String userId = (String) session.getAttributes().get("userId");
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("WS: userId is missing");
                session.close();
            } else {
                // save userId for this session
                session.getAttributes().put("userId", userId);
                sessions.add(session);
            }
            logger.info("WS: userSessions size: " + sessions.size());
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
        sessions.remove(session);
        logger.info("WS: remove session: " + session.toString());
        logger.info("WS: sessions size: " + sessions.size());
    }

    public void sendMessageToUser(String userId, Map<String, String> message) throws IOException {
        logger.info("WS: sendMessageToUser");
        logger.info("WS: userSessions: " + sessions.size());

        for (WebSocketSession session : sessions) {
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
