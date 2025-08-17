package io.fptu.sep490.security;

import io.fptu.sep490.utils.JsonUtils;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        int httpStatus;
        int errorCode;
        String messageKey = authException.getMessage();

        if ("auth.token.invalid".equalsIgnoreCase(messageKey)) {
            httpStatus = HttpServletResponse.SC_UNAUTHORIZED;
            errorCode = 498;
        } else if ("auth.token.expired".equalsIgnoreCase(messageKey)) {
            httpStatus = HttpServletResponse.SC_UNAUTHORIZED;
            errorCode = 40101;
        } else if ("auth.token.signature.invalid".equalsIgnoreCase(messageKey)) {
            httpStatus = HttpServletResponse.SC_UNAUTHORIZED;
            errorCode = 40102;
        } else if (authException instanceof BadCredentialsException) {
            httpStatus = HttpServletResponse.SC_BAD_REQUEST;
            errorCode = 400;
        } else {
            httpStatus = HttpServletResponse.SC_UNAUTHORIZED;
            errorCode = 401;
        }

        response.setStatus(httpStatus);
        response.setContentType("application/json;charset=UTF-8");

        var base = ResponseUtils.error(
                errorCode,
                LocalizedTextUtils.getLocalizedText(messageKey)
        );

        response.getWriter().write(JsonUtils.marshal(base));
    }
}
