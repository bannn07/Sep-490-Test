package io.fptu.sep490.service;

import io.fptu.sep490.dto.request.*;
import io.fptu.sep490.dto.response.BaseResponse;
import io.fptu.sep490.dto.response.OtpResponse;
import io.fptu.sep490.dto.response.UserDetailResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    
    UserDetailResponse registerUser(RegisterRequest request);

    UserDetailResponse loginUser(LoginRequest request);

    UserDetailResponse refreshAccessToken(RefreshTokenRequest request);

    OtpResponse checkOtp(OtpRequest request);

    OtpResponse sendOtp(OtpRequest request) throws MessagingException;

    BaseResponse<?> resetPassword(@Valid ResetPassRequest request);
}
