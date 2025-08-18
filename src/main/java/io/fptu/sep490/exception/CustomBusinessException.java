package io.fptu.sep490.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomBusinessException extends RuntimeException {

    private String errorCode;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }


}
