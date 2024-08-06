package com.ems.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ems.application.entity.Token;

public interface TokenRepository
        extends JpaRepositoryBase<Token, Integer>, JpaSpecificationExecutor<Token> {

    Optional<Token> findByToken(String token);

    List<Token> findByTokenAndIsDeletedFalse(String token);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId")
    List<Token> findAllByUserId(@Param("userId") Integer userId);
}
