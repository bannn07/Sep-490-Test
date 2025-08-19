package io.fptu.sep490.controller.user;

import io.fptu.sep490.dto.request.LoginRequest;
import io.fptu.sep490.dto.request.RefreshTokenRequest;
import io.fptu.sep490.dto.request.RegisterRequest;
import io.fptu.sep490.dto.response.BaseResponse;
import io.fptu.sep490.dto.response.UserDetailResponse;
import io.fptu.sep490.service.AccountService;
import io.fptu.sep490.utils.ResponseUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<UserDetailResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        UserDetailResponse account = accountService.registerUser(request);
        return ResponseEntity.ok(ResponseUtils.success(account));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserDetailResponse>> loginUser(
            @Valid @RequestBody LoginRequest request) {
        UserDetailResponse account = accountService.loginUser(request);
        return ResponseEntity.ok(ResponseUtils.success(account));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<UserDetailResponse>> refreshAccessToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        UserDetailResponse account = accountService.refreshAccessToken(request);
        return ResponseEntity.ok(ResponseUtils.success(account));
    }

}
