package io.fptu.sep490.exception.handler;

import io.fptu.sep490.dto.BaseResponse;
import io.fptu.sep490.utils.JsonUtils;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.ResponseUtils;
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

        BaseResponse<Object> baseResponse = ResponseUtils.error(
                HttpStatus.FORBIDDEN.value()
                , LocalizedTextUtils.getLocalizedText("access.denied")
        );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtils.marshal(baseResponse));
    }
}