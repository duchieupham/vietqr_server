package com.vietqr.org.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private static final Logger logger = Logger.getLogger(WebSocketConfig.class);

    @Autowired
    private SocketHandler socketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(socketHandler, "/socket")
                .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        if (request instanceof ServletServerHttpRequest) {
                            // LISTEN WEB SOCKET - DECLARE BY parameters.
                            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                            logger.info("WS: request: " + request.getURI().toString());
                            // PUSH NOTI TO USER
                            String userId = servletRequest.getServletRequest().getParameter("userId");
                            if (userId != null && !userId.trim().isEmpty()) {
                                logger.info("WS: beforeHandshake - userId: " + userId);
                                attributes.put("userId", userId);
                                return true;
                            }
                            // FOR LOGIN BY WEB BY LOGIN ID
                            String loginId = servletRequest.getServletRequest().getParameter("loginId");
                            if (loginId != null && !loginId.trim().isEmpty()) {
                                logger.info("WS: beforeHandshake - loginId: " + loginId);
                                attributes.put("loginId", loginId);
                                return true;
                            }
                            // FOR LOGIN BY EC (WORDPRESS)
                            String ecLoginId = servletRequest.getServletRequest().getParameter("ecLoginId");
                            if (ecLoginId != null && !ecLoginId.trim().isEmpty()) {
                                logger.info("WS: beforeHandshake - ecLoginId: " + ecLoginId);
                                attributes.put("ecLoginId", ecLoginId);
                                return true;
                            }
                            // FOR CHECK TRANSACTION STATUS
                            String transactionRefId = servletRequest.getServletRequest().getParameter("refId");
                            if (transactionRefId != null && !transactionRefId.trim().isEmpty()) {
                                logger.info("WS: beforeHandshake - transactionRefId: " + transactionRefId);
                                attributes.put("refId", transactionRefId);
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Exception ex) {
                    }
                });
    }
}
