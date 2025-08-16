package io.fptu.sep490.controller;

import io.fptu.sep490.dto.BaseResponse;
import io.fptu.sep490.dto.request.RegisterRequest;
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
    

}
