package io.fptu.sep490.service.impl;

import io.fptu.sep490.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, String> redisTemplate;

    private final Duration otpTTL = Duration.ofMinutes(2);
    private final Duration verifyTTL = Duration.ofMinutes(2);
    private static final int MAX_OTP_PER_DAY = 3;

    @Override
    public void saveOtp(String email, String otp) {
        String key = buildKey(email);
        redisTemplate.opsForValue().set(key, otp, otpTTL);
    }

    @Override
    public boolean verifyOtp(String email, String inputOtp) {
        var key = buildKey(email);
        var storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(inputOtp)) {
            return false;
        }

        redisTemplate.delete(key);
        return true;
    }

    private String buildKey(String email) {
        return "otp:" + email;
    }

    @Override
    public void saveOtpVerified(String email) {
        var verifyKey = buildVerifyKey(email);
        redisTemplate.opsForValue().set(verifyKey, "true", verifyTTL);
    }

    @Override
    public boolean isOtpVerified(String email) {
        var verifyKey = buildVerifyKey(email);
        return "true".equals(redisTemplate.opsForValue().get(verifyKey));
    }

    @Override
    public void clearOtpVerified(String email) {
        redisTemplate.delete(buildVerifyKey(email));
    }

    private String buildVerifyKey(String email) {
        return "otp:verified:" + email;
    }

    @Override
    public void lockOtpRequest(String email) {
        var lockKey = "otp:lock:" + email;
        redisTemplate.opsForValue().set(lockKey, "locked", Duration.ofSeconds(60));
    }

    @Override
    public boolean isOtpLocked(String email) {
        var lockKey = "otp:lock:" + email;
        return redisTemplate.hasKey(lockKey);
    }


    @Override
    public boolean checkOtpLimit(String email) {
        var today = LocalDate.now().toString();
        var redisKey = "otp:limit:" + email + ":" + today;

        var count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, Duration.ofDays(1));
        }

        return count != null && count <= MAX_OTP_PER_DAY;
    }

    @Override
    public void clearOtp(String email) {
        redisTemplate.delete(buildKey(email));
    }


    @Override
    public void rollbackOtpLimit(String email) {
        var today = LocalDate.now().toString();
        var redisKey = "otp:limit:" + email + ":" + today;
        redisTemplate.opsForValue().decrement(redisKey);
    }

    @Override
    public long getOtpTTLMinutes() {
        return otpTTL.toMinutes();
    }

    @Override
    public long getOtpTTLSeconds() {
        return otpTTL.toSeconds();
    }

}
