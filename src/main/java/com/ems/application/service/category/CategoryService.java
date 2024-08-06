package com.ems.application.service.category;

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

import com.ems.application.dto.category.CategoryListSearchCriteria;
import com.ems.application.dto.category.CategoryResponse;
import com.ems.application.dto.category.NewCategoryRequest;
import com.ems.application.entity.Category;
import com.ems.application.mapping.category.CategoryMapping;
import com.ems.application.repository.CategoryRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class CategoryService extends BaseService {

    private final CategoryRepository categoryRepository;
    private final HashIdsUtils hashIdsUtils;

    public CategoryService(CategoryRepository categoryRepository,
            HashIdsUtils hashIdsUtils) {
        this.categoryRepository = categoryRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    // Create new category entity
    public ResponseEntity<CategoryResponse> createNewCategory(NewCategoryRequest categoryRequest) {
        Category dtbCategoryEntity = new Category();
        Category dtbCategory = CategoryMapping.convertToEntity(categoryRequest, dtbCategoryEntity);
        categoryRepository.save(dtbCategory);
        return ResponseEntity.ok(CategoryMapping.convertToDto(dtbCategory, hashIdsUtils));
    }

    public ResponseEntity<Page<CategoryResponse>> getAllCategory(CategoryListSearchCriteria criteria) {
        // Create pageable object for pagination and sorting
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));
        // Retrieve a page of category entities based on the search criteria
        Page<Category> categoryPages = categoryRepository.findAll(
                (root, query, builder) -> {
                    // Ensure distinct results
                    query.distinct(true);
                    // Set predicates based on the search criteria
                    List<Predicate> predicates = setCategoryListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        // Convert the retrieved category entities to DTO
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        categoryPages
                .forEach(category -> categoryResponseList.add(CategoryMapping.convertToDto(category, hashIdsUtils)));
        Page<CategoryResponse> modifiedCategoryPages = new PageImpl<>(categoryResponseList, pageable,
                categoryPages.getTotalElements());

        return ResponseEntity.ok(modifiedCategoryPages);
    }

    protected List<Predicate> setCategoryListPredicate(
            Root<Category> root, CategoryListSearchCriteria criteria, CriteriaBuilder builder) {

        // Initialize a list to hold predicates
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates for filtering by creation date from,if provided
        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }
        // Add predicates for filtering by creation date to,if provided
        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"),
                    dateTo);

            predicates.add(dateToPredicate);
        }
        // Add predicates for filtering by deletion status
        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        predicates.add(isDeletedPredicate);
        if (criteria.getStatus()) {
            // Add predicates for filtering by status (active/inactive
            Path<Boolean> statusPath = root.get("status");
            Predicate statusPredicate = builder.equal(statusPath.as(Boolean.class),
                    true);
            predicates.add(statusPredicate);
        }
        // Add predicate for filtering by category name, if provided
        if (org.springframework.util.StringUtils.hasText(criteria.getCategoryName())) {
            Path<String> categoryNamePath = root.get("categoryName");
            Predicate categoryNamePredicate = builder.like(categoryNamePath, "%" +
                    criteria.getCategoryName() + "%");
            predicates.add(categoryNamePredicate);
        }

        return predicates;
    }

    public ResponseEntity<CategoryResponse> getCategoryById(String id) {
        Category category = categoryRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // check category is null
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        // convert category to DTO
        return ResponseEntity.ok(CategoryMapping.convertToDto(category, hashIdsUtils));
    }

    public ResponseEntity<CategoryResponse> updateCategory(String id, NewCategoryRequest categoryRequest) {
        // lấy danh mục từ cơ sở dữ liệu bằng ID
        Category category = categoryRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // Kiểm tra xem danh mục có tồn tại không
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        // Chuyển đổi thông tin mới của danh mục thành 1 đôi tượng Category
        Category updatedCategory = CategoryMapping.convertToEntity(categoryRequest, category);
        // Lưu danh mục đã được cập nhập vào cơ sở dữ liệu
        categoryRepository.save(updatedCategory);
        // Trả về danh mục đã được cập nhập dang DTO và trạng thái OK 200
        return ResponseEntity.ok(CategoryMapping.convertToDto(updatedCategory, hashIdsUtils));
    }

    public ResponseEntity<CategoryResponse> deleteCategory(String id) {
        // Lấy danh mục từ cơ sở dữ liệu bằng ID được giải mã
        Category category = categoryRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // Kiểm tra xem danh mục có tồn tại không
        if (category == null) {
            // Trả về 404 Not found nếu không tìm thấy danh mục
            return ResponseEntity.notFound().build();
        }
        // Đánh dấu danh mục là đã bị xóa
        category.setDeleted(true);
        // Lưu danh mục đã được đánh dấu xóa vào cơ sở dữ liệu
        categoryRepository.save(category);
        // Trả về danh mục đã được xóa ở dạng DTO và trạng thái 200 OK
        return ResponseEntity.ok(CategoryMapping.convertToDto(category, hashIdsUtils));
    }
}