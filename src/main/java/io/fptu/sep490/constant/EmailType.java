package io.fptu.sep490.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailType {

    SEND_OTP("Mã OTP của bạn", "email/send_otp");

    private final String subject;
    private final String templateName;
}
