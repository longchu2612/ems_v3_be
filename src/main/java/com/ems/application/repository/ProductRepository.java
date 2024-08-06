package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Product;

@Repository
public interface ProductRepository extends JpaRepositoryBase<Product, Integer>,
        JpaSpecificationExecutor<Product> {
    List<Product> findByIsDeletedFalse();
}
