package io.fptu.sep490.service;

import io.fptu.sep490.dto.request.LoginRequest;
import io.fptu.sep490.dto.request.RefreshTokenRequest;
import io.fptu.sep490.dto.request.RegisterRequest;
import io.fptu.sep490.dto.response.UserDetailResponse;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    
    UserDetailResponse registerUser(RegisterRequest request);

    UserDetailResponse loginUser(LoginRequest request);

    UserDetailResponse refreshAccessToken(RefreshTokenRequest request);
    
}
