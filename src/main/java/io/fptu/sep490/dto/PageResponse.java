package io.fptu.sep490.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageResponse {
    private Integer totalPage;
    private Integer totalItems;
    private Integer currentPage;
    private Integer pageSize;
}