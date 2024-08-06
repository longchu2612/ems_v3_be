package com.ems.application.dto.order;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import com.ems.application.dto.orderdetail.OrderDetailResponse;

@Getter
@Setter
public class OrdersResponse {
    private String id;
    private String tableName;
    private String createdAt;
    private String createdBy;
    private Integer totalQuantity;
    private Double totalPrice;
    private String customerName;
    private Integer tableId;
    private Integer status;
    List<OrderDetailResponse> orderDetails;
}
