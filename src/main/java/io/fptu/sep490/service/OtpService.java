package io.fptu.sep490.service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {

    void saveOtp(String email, String otp);

    boolean verifyOtp(String email, String inputOtp);

    void saveOtpVerified(String email);

    boolean isOtpVerified(String email);

    void clearOtpVerified(String email);

    void lockOtpRequest(String email);

    boolean isOtpLocked(String email);

    boolean checkOtpLimit(String email);

    void clearOtp(String email);

    // rollback quota (giảm count đi 1)
    void rollbackOtpLimit(String email);

    long getOtpTTLMinutes();

    long getOtpTTLSeconds();
}
