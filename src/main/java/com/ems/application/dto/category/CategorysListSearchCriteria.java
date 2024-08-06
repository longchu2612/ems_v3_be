package com.ems.application.dto.category;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorysListSearchCriteria extends SearchCriteria{
    private String categoryName;
}
