package io.fptu.sep490.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // ================= Business / Custom =================

    BUS_NOT_FOUND("BUS_001", "Resource not found"),
    BUS_DUPLICATE("BUS_002", "Duplicate resource"),
    BUS_GENERIC_ERROR("BUS_999", "Business error"),
    VAL_PARAM_INVALID("VAL_002", "Parameter or path variable is invalid"),

    // ================= System / Runtime =================

    SYS_DB_ERROR("SYS_001", "Database or persistence error"),
    SYS_RUNTIME_ERROR("SYS_002", "System runtime error"),
    SYS_ILLEGAL_ACCESS("SYS_005", "Illegal access"),
    SYS_UNKNOWN_ERROR("SYS_999", "Unexpected system error");

    private final String code;
    private final String description;

}


