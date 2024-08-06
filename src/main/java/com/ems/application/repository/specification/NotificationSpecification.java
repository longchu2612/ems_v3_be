package com.ems.application.repository.specification;

import java.util.Objects;

import javax.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import com.ems.application.entity.Notification;
import com.ems.application.entity.User;

public class NotificationSpecification extends BaseSpecification<Notification> {

    public static Specification<Notification> getByUserId(Integer userId) {
        if (Objects.isNull(userId)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Join<Notification, User> userJoin = root.join("user");
            return criteriaBuilder.equal(userJoin.get("id"), userId);
        };
    }
}
