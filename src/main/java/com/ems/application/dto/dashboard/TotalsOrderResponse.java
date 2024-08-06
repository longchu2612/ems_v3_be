package com.ems.application.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalsOrderResponse {
    private String categoryName;
    private Integer total;
}
