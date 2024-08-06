package com.ems.application.dto.common;

import com.ems.application.validator.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCriteria extends PaginationRequest {
    @DateTimeFormat(name = "dateFrom")
    private String dateFrom;

    @DateTimeFormat(name = "dateTo")
    private String dateTo;
}
