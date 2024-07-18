package com.vietqr.org.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String LOCK_PREFIX = "idempotency-lock:";

    @Override
    public Optional<String> getResponseForKey(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean deleteResponseForKey(String key) {
        boolean result = false;
        String lockKey = LOCK_PREFIX + key;
        try {
            redisTemplate.delete(lockKey);
            result = true;
        } catch (Exception ignored) {
        }
        return result;
    }

    @Override
    public boolean saveResponseForKey(String key, String response) {
        boolean result = false;
        String lockKey = LOCK_PREFIX + key;
        result = Boolean.TRUE.equals(redisTemplate
                .opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(30)));
        return result;
    }
}
