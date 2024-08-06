package com.ems.application.dto.table;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableListSearchCriteria extends SearchCriteria {
    private String name;
    private Integer status = 0;
}
