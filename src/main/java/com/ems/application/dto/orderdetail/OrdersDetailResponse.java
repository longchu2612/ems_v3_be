package com.ems.application.dto.orderdetail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrdersDetailResponse {
    private String id;
    private String note;
    private Integer status;
    private String productId;
    private String productName;
    private String image;
    private Integer quantity;
    private Double price;
}
