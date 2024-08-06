package com.ems.application.dto.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationRequest {

    private Integer pageIndex = 0;
    private Integer pageSize = 10;
    private String sortBy = "createdAt";
    private boolean descending = true;
}
