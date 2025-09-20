package io.fptu.ClubSpiral.common.exception.handler;

import io.fptu.ClubSpiral.common.utils.JsonUtils;
import io.fptu.ClubSpiral.common.utils.LocalizedTextUtils;
import io.fptu.ClubSpiral.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        var baseResponse = ResponseUtils.error(
                String.valueOf(HttpStatus.FORBIDDEN)
                , LocalizedTextUtils.getLocalizedText("access.denied")
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.marshal(baseResponse));
    }
}