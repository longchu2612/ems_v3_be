package com.ems.application.dto.dashboard;

import java.math.BigInteger;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountByDashboardCategoryResponse extends SearchCriteria {
    private Double revenue;
    private BigInteger totalOrder;
    private BigInteger topProduct;
}
