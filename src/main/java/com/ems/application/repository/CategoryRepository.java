package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepositoryBase<Category, Integer>,
        JpaSpecificationExecutor<Category> {
    List<Category> findByIsDeletedFalse();
}
