package com.ems.application.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatesOrderRequest {
    private String id;
    private String tableId;
    private String note;
    private String customerName;
    private Integer status;
}
