package com.ems.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.User;

@Repository
public interface UserRepository extends JpaRepositoryBase<User, Integer>,
        JpaSpecificationExecutor<User> {
    Optional<User> findByUserNameAndIsDeletedFalse(String username);
}
