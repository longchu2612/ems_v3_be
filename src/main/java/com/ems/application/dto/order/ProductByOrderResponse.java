package com.ems.application.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductByOrderResponse {
    private String id;
    private String name;
    private String note;
    private Integer quantity;
    private Integer done;
    private Integer inProgress;
    private Integer serving;
}
