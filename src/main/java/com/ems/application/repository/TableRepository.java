package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Tables;

@Repository
public interface TableRepository extends JpaRepositoryBase<Tables, Integer>,
        JpaSpecificationExecutor<Tables> {
    Tables findByIdAndStatus(Integer id, Integer status);

    Tables findByName(String name);

    List<Tables> findByIsDeletedFalse();
}
