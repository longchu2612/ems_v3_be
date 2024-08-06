package com.ems.application.mapping.category;

import com.ems.application.dto.category.CategoryResponse;
import com.ems.application.dto.category.NewCategoryRequest;
import com.ems.application.entity.Category;
import com.ems.application.util.HashIdsUtils;

public class CategoryMapping {
    public static Category convertToEntity(NewCategoryRequest categoryRequest, Category dtbCategory) {
        dtbCategory.setCategoryName(categoryRequest.getCategoryName());
        dtbCategory.setStatus(categoryRequest.getStatus());
        return dtbCategory;
    }

    public static CategoryResponse convertToDto(Category dtbCategory, HashIdsUtils hashIdsUtils) {
        CategoryResponse response = new CategoryResponse();
        response.setId(hashIdsUtils.encodeId(dtbCategory.getId()));
        response.setCategoryName(dtbCategory.getCategoryName());
        response.setStatus(dtbCategory.getStatus());
        return response;
    }

}
