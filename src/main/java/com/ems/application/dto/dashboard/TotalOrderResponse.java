package com.ems.application.dto.dashboard;

import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalOrderResponse {
    private String categoryName;
    private BigInteger total;
}
