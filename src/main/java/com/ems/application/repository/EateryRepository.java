package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Eatery;

@Repository
public interface EateryRepository extends JpaRepositoryBase<Eatery, Integer>,
        JpaSpecificationExecutor<Eatery> {
    List<Eatery> findByIsDeletedFalse();
}
