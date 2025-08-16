package io.fptu.sep490.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "signup.name.required")
    private String userName;

    @NotBlank(message = "signup.email.required")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "signup.email.invalid"
    )
    private String email;

    @NotBlank(message = "signup.password.required")
    @Size(min = 6, message = "signup.password.min")
    private String password;

    @NotBlank(message = "signup.password.confirm.required")
    private String confirmPassword;

    private String role;
}
