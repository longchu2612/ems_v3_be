package com.ems.application.repository.specification;

import java.util.Objects;
import javax.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.ems.application.entity.Role;
import com.ems.application.entity.User;

public class UserSpecification extends BaseSpecification<User> {

    public static Specification<User> getUserByRole(Integer roleId) {
        if (Objects.isNull(roleId)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Join<User, Role> userRole = root.join("roles");
            return criteriaBuilder.equal(userRole.get("id"), roleId);
        };
    }

    public static Specification<User> getUserByDeleted(Boolean isDeleted) {
        if (Objects.isNull(isDeleted)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"),
                isDeleted);
    }

    public static Specification<User> getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                root.get("userName").as(String.class), "%" + username + "%");
    }
}
