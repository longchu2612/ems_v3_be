package com.ems.application.dto.table;

import lombok.Getter;
import lombok.Setter;
import com.ems.application.dto.order.OrderResponse;

@Getter
@Setter
public class TablesResponse {
    
    private String id;
    private String name;
    private String position;
    private Boolean status;
    private OrderResponse order;
}
