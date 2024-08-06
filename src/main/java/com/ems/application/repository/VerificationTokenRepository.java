package com.ems.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ems.application.entity.VerificationToken;

public interface VerificationTokenRepository extends
        JpaRepositoryBase<VerificationToken, Integer>,
        JpaSpecificationExecutor<VerificationToken> {

    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.token=:token")
    void deleteByToken(@Param("token") String token);
}
