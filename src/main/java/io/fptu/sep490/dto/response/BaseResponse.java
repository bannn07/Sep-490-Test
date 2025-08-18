package io.fptu.sep490.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Builder
@Data
public class BaseResponse<T> {
    private String errorCode;
    private T data;
    private Object page;
    private String message;

    @Builder.Default
    private boolean success = true;

    public <E> BaseResponse<T> withPageData(Page<E> page) {
        this.page = PageResponse.builder()
                .totalPage(page.getTotalPages())
                .totalItems((int) page.getTotalElements())
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .build();
        return this;
    }
}