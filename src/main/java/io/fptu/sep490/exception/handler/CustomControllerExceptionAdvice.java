package io.fptu.sep490.exception.handler;

import io.fptu.sep490.constant.StatusCodeConstants;
import io.fptu.sep490.dto.BaseResponse;
import io.fptu.sep490.exception.DuplicateResourceException;
import io.fptu.sep490.exception.IllegalArgumentException;
import io.fptu.sep490.utils.LocalizedTextUtils;
import io.fptu.sep490.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);

        // 1. Xử lý BindException (lỗi validate DTO)
        if (e instanceof BindException bindException) {
            String message = bindException.getBindingResult().getFieldErrors().stream()
                    .map(error -> LocalizedTextUtils.getLocalizedText(error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
            BaseResponse<Object> response = ResponseUtils.error(
                    StatusCodeConstants.MANDATORY_PARAM_ERROR, message);
            return ResponseEntity.ok(response);
        }

        // 2. Xử lý HandlerMethodValidationException (lỗi validate method param)
        if (e instanceof HandlerMethodValidationException validationException) {
            String message = validationException.getValueResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream())
                    .map(err -> LocalizedTextUtils.getLocalizedText(err.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
            BaseResponse<Object> response = ResponseUtils.error(
                    StatusCodeConstants.MANDATORY_PARAM_ERROR, message);
            return ResponseEntity.ok(response);
        }

        // 3. Xử lý lỗi khác (Internal Server Error)
        BaseResponse<Object> response = ResponseUtils.error(
                StatusCodeConstants.INTERNAL_SERVER_ERROR,
                LocalizedTextUtils.getLocalizedText("internal.server.error")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.CONFLICT.value(), e.getMessage())
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalizedTextUtils.getLocalizedText("auth.authenticate.fail")
                )
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

}