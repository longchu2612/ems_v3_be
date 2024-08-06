package com.ems.application.dto.product;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductListSearchCriteria extends SearchCriteria {
    private String name;
    private String categoryId;
    private Boolean status = false;
}
