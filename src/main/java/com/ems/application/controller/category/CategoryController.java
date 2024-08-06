package com.ems.application.controller.category;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.category.CategoryListSearchCriteria;
import com.ems.application.dto.category.CategoryResponse;
import com.ems.application.dto.category.NewCategoryRequest;
import com.ems.application.service.category.CategoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Category")

@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApiOperation(value = "Add new category")
    @PostMapping(value = "/add")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody NewCategoryRequest categoryRequest) {
        return categoryService.createNewCategory(categoryRequest);
    }

    @ApiOperation(value = "Get list category ")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<CategoryResponse>> getCategories(
            @Valid @RequestBody CategoryListSearchCriteria criteria) {
        return categoryService.getAllCategory(criteria);
    }

    @ApiOperation(value = "Get category by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable("id") String id) {
        return categoryService.getCategoryById(id);
    }

    @ApiOperation(value = "Update category")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable("id") String id,
            @Valid @RequestBody NewCategoryRequest categoryRequest) {
        return categoryService.updateCategory(id, categoryRequest);
    }

    @ApiOperation(value = "Delete category")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable("id") String id) {
        return categoryService.deleteCategory(id);
    }
}
