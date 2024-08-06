package com.ems.application.dto.order;

import java.util.List;

import com.ems.application.dto.orderdetail.OrderDetailResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private String id;
    private String tableName;
    // private String createdAt;
    private String createdBy;
    private Integer totalQuantity;
    private Double totalPrice;
    private String customerName;
    private Integer tableId;
    private Integer status;
    private String position;
    private String createdAt;
    private String paymentMethod;
    private String cashierName;
    List<OrderDetailResponse> orderDetails;

}
