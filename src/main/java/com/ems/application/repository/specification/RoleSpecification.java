package com.ems.application.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.ems.application.entity.Role;

public class RoleSpecification extends BaseSpecification<Role> {

    public static Specification<Role> getRoleByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                root.get("roleName").as(String.class), "%" + name + "%");
    }
}
