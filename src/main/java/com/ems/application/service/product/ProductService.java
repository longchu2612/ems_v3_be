package com.ems.application.service.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.product.NewProductRequest;
import com.ems.application.dto.product.ProductListSearchCriteria;
import com.ems.application.dto.product.ProductResponse;
import com.ems.application.entity.Category;
import com.ems.application.entity.Product;
import com.ems.application.mapping.product.ProductMapping;
import com.ems.application.repository.CategoryRepository;
import com.ems.application.repository.ProductRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class ProductService extends BaseService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final HashIdsUtils hashIdsUtils;

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            HashIdsUtils hashIdsUtils) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    // Create new product
    public ResponseEntity<ProductResponse> createNewProduct(NewProductRequest productRequest) {
        // Retrieve the category from the repository based on the decoded category ID
        Category category = categoryRepository.findById(hashIdsUtils.decodeId(productRequest.getCategoryId()))
                .orElse(null);
        // Check if the category exists; return a bad request response if not
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }
        // Set the retrieved category to the product request
        productRequest.setCategory(category);
        Product dtbProductEntity = new Product();
        Product dtbProduct = ProductMapping.convertToEntity(productRequest, dtbProductEntity);
        productRepository.save(dtbProduct);
        return ResponseEntity.ok(ProductMapping.convertToDto(dtbProduct, hashIdsUtils));
    }

    public ResponseEntity<Page<ProductResponse>> getAllProduct(ProductListSearchCriteria criteria) {
        // Create object Pageable and Sort

        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));
        // Get the Product list combined with pagination
        Page<Product> productPages = productRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setProductListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        // Generate a list of object ProductResponse
        List<ProductResponse> productResponseList = new ArrayList<>();

        for (Product product : productPages.getContent()) {
            Category category = product.getCategory();
            if (category == null) {
                continue;
            }
            ProductResponse productResponse = ProductMapping.convertToDto(product, hashIdsUtils);
            productResponse.setCategoryName(category.getCategoryName());
            productResponse.setCategoryId(hashIdsUtils.encodeId(category.getId()));
            productResponseList.add(productResponse);
        }
        Page<ProductResponse> modifiedProductPages = new PageImpl<>(productResponseList, pageable,
                productPages.getTotalElements());
        return ResponseEntity.ok(modifiedProductPages);
    }

    protected List<Predicate> setProductListPredicate(
            Root<Product> root, ProductListSearchCriteria criteria, CriteriaBuilder builder) {

        // Initialize a list to hold predicates
        List<Predicate> predicates = new ArrayList<>();
        if (org.springframework.util.StringUtils.hasText(criteria.getName())) {
            Path<String> productNamePath = root.get("name");

            Predicate productNamePredicate = builder.like(productNamePath, "%" +
                    criteria.getName() + "%");
            predicates.add(productNamePredicate);
        }
        // Add predicate for filter by date from
        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }
        if (org.springframework.util.StringUtils.hasText(criteria.getCategoryId())) {
            Path<String> categoryIdPath = root.get("category").get("id");
            Predicate categoryIdPredicate = builder.equal(categoryIdPath,
                    hashIdsUtils.decodeId(criteria.getCategoryId()));
            predicates.add(categoryIdPredicate);
        }
        // Add predicate for filter by date to

        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"),
                    dateTo);

            predicates.add(dateToPredicate);
        }
        // Add predicate for filter by is_delete
        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        predicates.add(isDeletedPredicate);
        if (criteria.getStatus()) {
            // Add predicate for filter by status (active/inactive)
            Path<Boolean> statusPath = root.get("status");
            Predicate statusPredicate = builder.equal(statusPath.as(Boolean.class),
                    criteria.getStatus());
            predicates.add(statusPredicate);

        }
        // Add predicate for filter by name
        if (org.springframework.util.StringUtils.hasText(criteria.getName())) {
            Path<String> productNamePath = root.get("name");
            Predicate productNamePredicate = builder.like(productNamePath, "%" +
                    criteria.getName() + "%");
            predicates.add(productNamePredicate);
        }
        // Return predicates
        return predicates;
    }

    public ResponseEntity<ProductResponse> getProductById(String id) {
        // Find product by id
        Product product = productRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // check product is null
        if (product == null) {
            // Return not found product
            return ResponseEntity.notFound().build();
        }
        // Convert to DTO product and return in a ResponseEntity
        return ResponseEntity.ok(ProductMapping.convertToDto(product, hashIdsUtils));
    }

    public ResponseEntity<ProductResponse> updateProduct(String id, NewProductRequest productRequest) {
        // Find product by Id
        Product product = productRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().build();
        }
        Category category = categoryRepository.findById(hashIdsUtils.decodeId(productRequest.getCategoryId()))
                .orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }
        productRequest.setCategory(category);
        Product updatedProduct = ProductMapping.convertToEntity(productRequest, product);
        productRepository.save(updatedProduct);
        return ResponseEntity.ok(ProductMapping.convertToDto(updatedProduct, hashIdsUtils));
    }

    // public ResponseEntity<ProductResponse> deleteProduct(String id) {
    // Product product =
    // productRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
    // if (product == null) {
    // return ResponseEntity.notFound().build();
    // }
    // product.setDeleted(true);
    // productRepository.save(product);
    // return ResponseEntity.ok(ProductMapping.convertToDto(product, hashIdsUtils));
    // }

    public ResponseEntity<ProductResponse> deleteProduct(String id) {
        Product product = productRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        product.setDeleted(true);
        productRepository.save(product);
        return ResponseEntity.ok(ProductMapping.convertToDto(product, hashIdsUtils));
    }
}