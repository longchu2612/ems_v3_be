package com.ems.application.dto.dashboard;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopProductRequest extends SearchCriteria {
    private Integer startDate;
    private Integer endDate;
}
