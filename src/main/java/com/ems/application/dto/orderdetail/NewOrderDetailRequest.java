package com.ems.application.dto.orderdetail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewOrderDetailRequest {
    private String id;
    private String productId;
    private Integer quantity;
    private Integer pending;
    private Integer serving;
    private Integer progress;
    private Integer done;
    private String note;
    private Double price = 0.0;
    private Integer status;
}
