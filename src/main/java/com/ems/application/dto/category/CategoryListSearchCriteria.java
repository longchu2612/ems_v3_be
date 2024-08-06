package com.ems.application.dto.category;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryListSearchCriteria extends SearchCriteria {
    private String categoryName;
    private Boolean status = false;
}
