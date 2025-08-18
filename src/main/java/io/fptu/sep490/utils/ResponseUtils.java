package io.fptu.sep490.utils;

import io.fptu.sep490.dto.response.BaseResponse;
import org.springframework.data.domain.Page;

public class ResponseUtils {
    public static <T> BaseResponse<T> error(String errorCode, String message) {
        return build(false, null, message, errorCode);
    }

    public static <T> BaseResponse<T> success(String message) {
        return build(true, null, message, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return build(true, data, null, null);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return build(true, data, message, null);
    }

    public static <T, E> BaseResponse<T> success(T data, String message, Page<E> page) {
        return build(true, data, message, null).withPageData(page);
    }
    private static <T> BaseResponse<T> build(boolean success, T data, String message, String errorCode) {
        return BaseResponse.<T>builder()
                .success(success)
                .data(data)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
