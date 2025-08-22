package io.fptu.sep490.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpResponse {

    private String email;
    private int duration;
    private int otpTime;
}