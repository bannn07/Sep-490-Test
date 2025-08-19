package io.fptu.sep490.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenStoreService {

    void storeAccessToken(String token, long ttlMillis);

    void storeRefreshToken(String token, long ttlMillis);

    boolean isAccessTokenValid(String token);

    boolean isRefreshTokenValid(String token);

    void revokeAccessToken(String token);

    void revokeRefreshToken(String token);

}
