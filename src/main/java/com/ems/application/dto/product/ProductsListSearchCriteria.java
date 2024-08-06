package com.ems.application.dto.product;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsListSearchCriteria extends SearchCriteria {
    private String name;
}
