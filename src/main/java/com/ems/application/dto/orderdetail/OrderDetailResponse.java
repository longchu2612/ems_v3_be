package com.ems.application.dto.orderdetail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailResponse {
    private String id;
    private String note;
    private Integer status;
    private String productId;
    private String productName;
    private String image;
    private Integer quantity;
    private Double price;
    private Integer inProgress;
    private Integer done;
    private Integer pay;
    private Integer pending;
    private Integer serving;
    private String tableName;
}