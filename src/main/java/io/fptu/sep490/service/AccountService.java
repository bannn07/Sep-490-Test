package io.fptu.sep490.service;

import io.fptu.sep490.dto.request.RegisterRequest;
import io.fptu.sep490.dto.response.UserDetailResponse;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    
    UserDetailResponse registerUser(RegisterRequest request);
    
}
