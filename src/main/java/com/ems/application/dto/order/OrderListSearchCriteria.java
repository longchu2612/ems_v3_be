package com.ems.application.dto.order;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListSearchCriteria extends SearchCriteria {
    private String categoryName;
    private String cashierName;
    private Integer status;
}
