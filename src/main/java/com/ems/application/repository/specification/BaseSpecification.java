package com.ems.application.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.ems.application.entity.EntityBase;

public class BaseSpecification<T extends EntityBase> {

    public Specification<T> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("isDeleted").as(Boolean.class), false);
    }

    public Specification<T> equal(String fieldName, String fieldValue) {
        return (fieldValue == null)
                ? null
                : (root, query, cb) -> cb.equal(root.get(fieldName).as(String.class), fieldValue);
    }

    public Specification<T> equal(String fieldName, Integer fieldValue) {
        return (fieldValue == null)
                ? null
                : (root, query, cb) -> cb.equal(root.get(fieldName), fieldValue);
    }

    public Specification<T> contain(String fieldName, String fieldValue) {
        return (fieldValue == null || fieldValue.isEmpty())
                ? null
                : (root, query, cb) -> cb.like(root.get(fieldName).as(String.class), "%" + fieldValue + "%");
    }

    public Specification<T> containIgnore(String fieldName, String fieldValue) {
        return (fieldValue == null || fieldValue.isEmpty())
                ? null
                : (root, query, cb) -> cb.like(
                        cb.lower(root.get(fieldName)).as(String.class),
                        "%" + fieldValue.toLowerCase() + "%");
    }

    public Specification<T> contain(String fieldName, Integer fieldValue) {
        return (fieldValue == null)
                ? null
                : (root, query, cb) -> cb.like(root.get(fieldName).as(String.class), "%" + fieldValue + "%");
    }

    public Specification<T> isNotNull(String fieldName) {
        return (fieldName == null) ? null : (root, query, cb) -> cb.isNotNull(root.get(fieldName));
    }

    public Specification<T> notEqual(String fieldName, Integer fieldValue) {
        return (fieldValue == null)
                ? null
                : (root, query, cb) -> cb.notEqual(root.get(fieldName), fieldValue);
    }
}
