package com.ems.application.dto.order;

import java.util.List;

import com.ems.application.dto.orderdetail.NewOrderDetailRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsOrderRequest {

    private String tableId;
    private String note;
    private String customerName;
    private Integer status = 1;
    private List<NewOrderDetailRequest> orderDetails;
    private Double totalPrice;
    private Integer totalQuantity;
}
