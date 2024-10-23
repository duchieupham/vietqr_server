package com.vietqr.org.service.redis;

import com.vietqr.org.controller.TransactionBankController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Logger logger = Logger.getLogger(IdempotencyServiceImpl.class);

    private static final String LOCK_PREFIX = "idempotency-lock:";
    private static final String UUID_TRANS_PREFIX = "idempotency-uuid-lock:";

    @Override
    public Optional<String> getResponseForKey(String key) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(key));
        } catch (Exception e) {
            logger.error("getResponseForKey: ERROR " + e.getMessage());
        }
        return Optional.ofNullable(null);
    }

    @Override
    public boolean deleteResponseForKey(String key) {
        boolean result = false;
        String lockKey = LOCK_PREFIX + key;
        try {
            redisTemplate.delete(lockKey);
            result = true;
        } catch (Exception e) {
            logger.error("deleteResponseForKey: ERROR " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean saveResponseForKey(String key, String response, int duration) {
        boolean result = false;
        String lockKey = LOCK_PREFIX + key;
        try {
            result = Boolean.TRUE.equals(redisTemplate
                    .opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(duration)));
        } catch (Exception e) {
            logger.error("saveResponseForKey: ERROR " + e.getMessage());
        }
        return result;
    }

    @Override
    public boolean saveResponseForUUIDRefundKey(String key, String response, int duration) {
        boolean result = false;
        String lockKey = UUID_TRANS_PREFIX + key;
        try {
            result = Boolean.TRUE.equals(redisTemplate
                    .opsForValue().setIfAbsent(lockKey, response, Duration.ofSeconds(duration)));
        } catch (Exception e) {
            logger.error("saveResponseForUUIDRefundKey: ERROR " + e.getMessage());
        }
        return result;
    }

    public Optional<String> getResponseForUUIDRefundKey(String key) {
        String lockKey = UUID_TRANS_PREFIX + key;
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(lockKey));
        } catch (Exception e) {
            logger.error("getResponseForUUIDRefundKey ERROR: " + e.getMessage());
        }
        return Optional.ofNullable(null);
    }

    @Override
    public boolean deleteResponseForUUIDRefundKey(String key) {
        boolean result = false;
        String lockKey = UUID_TRANS_PREFIX + key;
        try {
            redisTemplate.delete(lockKey);
            result = true;
        } catch (Exception e) {
            logger.error("deleteResponseForUUIDRefundKey ERROR: " + e.getMessage());
        }
        return result;
    }
}
