package com.vietqr.org.service;

import org.springframework.stereotype.Service;

@Service
public interface MessengerService {
    public void sendMessage(String recipientId, String message);
}
