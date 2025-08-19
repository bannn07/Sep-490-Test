package io.fptu.sep490.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

    @JsonProperty
    @NotBlank(message = "auth.token.missing")
    private String refreshToken;

}