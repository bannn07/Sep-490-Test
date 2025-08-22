package io.fptu.sep490.controller.user;

import io.fptu.sep490.dto.request.OtpRequest;
import io.fptu.sep490.dto.request.ResetPassRequest;
import io.fptu.sep490.dto.response.BaseResponse;
import io.fptu.sep490.dto.response.OtpResponse;
import io.fptu.sep490.service.AccountService;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.ResponseUtils;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forgot-pass")
public class ForgotPassController {

    private final AccountService accountService;

    @PostMapping("/send-otp")
    public ResponseEntity<BaseResponse<OtpResponse>> sendOtp(@Valid @RequestBody OtpRequest request) throws MessagingException {
        OtpResponse response = accountService.sendOtp(request);
        return ResponseEntity.ok(ResponseUtils.success(response,
                LocalizedTextUtils.getLocalizedText("forgot-pass.success")));
    }

    @PostMapping("/check-otp")
    public ResponseEntity<BaseResponse<OtpResponse>> checkOtp(@Valid @RequestBody OtpRequest request) {
        OtpResponse response = accountService.checkOtp(request);
        return ResponseEntity.ok(ResponseUtils.success(response,
                LocalizedTextUtils.getLocalizedText("check-otp.success")));
    }

    @PutMapping("/reset-pass")
    public ResponseEntity<BaseResponse<?>> resetPass(@Valid @RequestBody ResetPassRequest request) {
        return ResponseEntity.ok(ResponseUtils.success(accountService.resetPassword(request),
                LocalizedTextUtils.getLocalizedText("reset.pass.success")));
    }

}
