package com.ems.application.dto.orderdetail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewOrderDetailsRequest {
    private String productId;
    private Integer quantity;
    private String note;
}
