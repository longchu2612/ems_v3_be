package com.ems.application.dto.product;

import com.ems.application.entity.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewProductRequest {

    private String name;
    private String categoryId;
    private Category category;
    private Double price;
    private Boolean status;
    private String image;
}
