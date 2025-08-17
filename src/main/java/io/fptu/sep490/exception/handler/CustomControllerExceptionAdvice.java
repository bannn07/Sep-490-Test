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
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice // Đánh dấu class này là global exception handler cho toàn bộ Controller
@Slf4j // Lombok tạo sẵn logger (log.info, log.error, ...)
public class CustomControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);

        // 1. Xử lý lỗi validate DTO (ví dụ @Valid trong RequestBody)
        if (e instanceof BindException bindException) {
            String message = bindException.getBindingResult().getFieldErrors().stream()
                    .map(error -> LocalizedTextUtils.getLocalizedText(error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));

            BaseResponse<Object> response = ResponseUtils.error(
                    StatusCodeConstants.MANDATORY_PARAM_ERROR, message);
            return ResponseEntity.ok(response);
        }

        // 2. Xử lý lỗi validate tham số trong method (ví dụ @RequestParam, @PathVariable)
        if (e instanceof HandlerMethodValidationException validationException) {
            String message = validationException.getValueResults().stream()
                    .flatMap(result -> result.getResolvableErrors().stream())
                    .map(err -> LocalizedTextUtils.getLocalizedText(err.getDefaultMessage()))
                    .collect(Collectors.joining(", "));

            BaseResponse<Object> response = ResponseUtils.error(
                    StatusCodeConstants.MANDATORY_PARAM_ERROR, message);
            return ResponseEntity.ok(response);
        }

        // 3. Trường hợp lỗi khác (Internal Server Error)
        BaseResponse<Object> response = ResponseUtils.error(
                StatusCodeConstants.INTERNAL_SERVER_ERROR,
                LocalizedTextUtils.getLocalizedText("internal.server.error")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    /**
     * Xử lý lỗi khi resource đã tồn tại (ví dụ insert trùng key)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage())
        );
    }

    /**
     * Xử lý lỗi tham số không hợp lệ do logic nghiệp vụ (custom IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.CONFLICT.value(), e.getMessage())
        );
    }

    /**
     * Xử lý lỗi xác thực sai username/password
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(
                        HttpStatus.BAD_REQUEST.value(),
                        LocalizedTextUtils.getLocalizedText("auth.authenticate.fail")
                )
        );
    }

    /**
     * Xử lý lỗi account bị disable (chưa active, bị vô hiệu hóa)
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledException(DisabledException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    /**
     * Xử lý lỗi account bị khóa (locked)
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException e) {
        return ResponseEntity.ok(
                ResponseUtils.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

}