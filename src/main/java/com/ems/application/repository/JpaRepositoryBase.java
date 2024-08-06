package com.ems.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.ems.application.entity.EntityBase;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface JpaRepositoryBase<T extends EntityBase, IdT extends Serializable>
        extends JpaRepository<T, IdT> {

    default void softDelete(T entity) {
        entity.setDeleted(true);
    }

    /**
     * @param entities List-T
     */
    default void softDeleteAll(List<T> entities) {
        for (T entity : entities) {
            entity.setDeleted(true);
        }
    }

    @Transactional
    default void forceDeleteAll(List<T> entities) {
        this.deleteAllInBatch(entities);
        this.flush();
    }

    default void forceDelete(T entity) {
        this.delete(entity);
    }
}
