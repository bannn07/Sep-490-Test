package io.fptu.sep490.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank(message = "signup.input.required")
    private String input;

    @NotBlank(message = "signup.password.required")
    @Size(min = 6, message = "signup.password.min")
    private String password;

}
