package com.ems.application.dto.table;

import com.ems.application.dto.order.OrderResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableResponse {

    private String id;
    private String name;
    private String position;
    private Integer status;
    private OrderResponse order;
}
