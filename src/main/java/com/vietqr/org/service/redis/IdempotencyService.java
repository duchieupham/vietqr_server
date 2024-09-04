package com.vietqr.org.service.redis;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IdempotencyService {

    public Optional<String> getResponseForKey(String key);
    public boolean deleteResponseForKey(String key);
    public boolean deleteResponseForUUIDRefundKey(String key);
    public boolean saveResponseForKey(String key, String response, int duration);
    public boolean saveResponseForUUIDRefundKey(String key, String response, int duration);
}
