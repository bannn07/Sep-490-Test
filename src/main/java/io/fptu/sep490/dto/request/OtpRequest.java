package io.fptu.sep490.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequest {

    @NotBlank(message = "signup.email.required")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "signup.email.invalid"
    )
    private String email;

    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "otp.invalid"
    )
    private String otp;
}