package com.ems.application.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.UserRole;

@Repository
public interface UserRoleRepository extends JpaRepositoryBase<UserRole, Integer>,
        JpaSpecificationExecutor<UserRole> {
    UserRole findByUserId(Integer userId);
}
