package com.ems.application.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductRequest {
    private String id;
    private String name;
    private Integer categoryId;
    private Double price;
    private String image;
}
