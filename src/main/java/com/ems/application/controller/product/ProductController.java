package com.ems.application.controller.product;

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

import com.ems.application.dto.product.NewProductRequest;
import com.ems.application.dto.product.ProductListSearchCriteria;
import com.ems.application.dto.product.ProductResponse;
import com.ems.application.service.product.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Product")

@RestController
@RequestMapping(value = "/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "Add new product")
    @PostMapping(value = "/add")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody NewProductRequest productRequest) {
        return productService.createNewProduct(productRequest);
    }

    @ApiOperation(value = "Get list product ")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Valid @RequestBody ProductListSearchCriteria criteria) {
        return productService.getAllProduct(criteria);
    }

    @ApiOperation(value = "Get product by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") String id) {
        return productService.getProductById(id);
    }

    @ApiOperation(value = "Update product")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") String id,
            @Valid @RequestBody NewProductRequest productRequest) {
        return productService.updateProduct(id, productRequest);
    }

    @ApiOperation(value = "Delete product")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable("id") String id) {
        return productService.deleteProduct(id);
    }
}
