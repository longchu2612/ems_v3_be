package com.ems.application.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {
    private Integer pageIndex = 1;
    private Integer pageSize = 100;
    private String sortBy = "createdAt";
    private boolean descending = true;
}
