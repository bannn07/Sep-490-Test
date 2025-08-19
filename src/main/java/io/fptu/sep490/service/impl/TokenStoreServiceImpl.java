package io.fptu.sep490.service.impl;

import io.fptu.sep490.service.TokenStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStoreServiceImpl implements TokenStoreService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ACCESS_PREFIX = "jwt:access:";
    private static final String REFRESH_PREFIX = "jwt:refresh:";

    @Override
    public void storeAccessToken(String token, long ttlMillis) {
        redisTemplate.opsForValue().set(ACCESS_PREFIX + token, "valid", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void storeRefreshToken(String token, long ttlMillis) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + token, "valid", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isAccessTokenValid(String token) {
        return redisTemplate.hasKey(ACCESS_PREFIX + token);
    }

    @Override
    public boolean isRefreshTokenValid(String token) {
        return redisTemplate.hasKey(REFRESH_PREFIX + token);
    }

    @Override
    public void revokeAccessToken(String token) {
        redisTemplate.delete(ACCESS_PREFIX + token);
    }

    @Override
    public void revokeRefreshToken(String token) {
        redisTemplate.delete(REFRESH_PREFIX + token);
    }


}
