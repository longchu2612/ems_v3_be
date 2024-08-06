package com.ems.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Role;

@Repository
public interface RoleRepository extends JpaRepositoryBase<Role, Integer>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByRoleName(String name);
}
