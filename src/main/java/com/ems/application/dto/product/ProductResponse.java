package com.ems.application.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {

    private String id;
    private String name;
    private String categoryName;
    private String categoryId;
    private Double price;
    private Boolean status;
    private String image;
}
