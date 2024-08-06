package com.ems.application.mapping.product;

import com.ems.application.dto.product.NewProductRequest;
import com.ems.application.dto.product.ProductResponse;
import com.ems.application.entity.Product;
import com.ems.application.util.HashIdsUtils;

public class ProductMapping {
    public static Product convertToEntity(NewProductRequest productRequest, Product dtbProduct) {
        dtbProduct.setName(productRequest.getName());
        dtbProduct.setCategory(productRequest.getCategory());
        dtbProduct.setPrice(productRequest.getPrice());
        dtbProduct.setImage(productRequest.getImage());
        dtbProduct.setStatus(productRequest.getStatus());
        return dtbProduct;
    }

    public static ProductResponse convertToDto(Product dtbProduct, HashIdsUtils hashIdsUtils) {
        ProductResponse response = new ProductResponse();
        response.setId(hashIdsUtils.encodeId(dtbProduct.getId()));
        response.setName(dtbProduct.getName());
        response.setCategoryId(hashIdsUtils.encodeId(dtbProduct.getCategory().getId()));
        response.setCategoryName(dtbProduct.getCategory().getCategoryName());
        response.setPrice(dtbProduct.getPrice());
        response.setStatus(dtbProduct.getStatus());
        response.setImage(dtbProduct.getImage());
        return response;
    }

}
