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
        logger.info("WS: add session: " + session.toString());
        sessions.add(session);
        logger.info("WS: sessions size: " + sessions.size());
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
        logger.info("WS: sessions: " + sessions.size());

        for (WebSocketSession session : sessions) {
            logger.info("WS: session ID: " + session.getId());
            logger.info("WS: session Attributes: " + session.getAttributes());
            logger.info("WS: session userId: " + session.getAttributes().get("userId"));
            // if (session.getAttributes().get("userId").equals(userId)) {
            // ObjectMapper mapper = new ObjectMapper();
            // String jsonMessage = mapper.writeValueAsString(message);
            // session.sendMessage(new TextMessage(jsonMessage));
            // }
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        }
    }
}
