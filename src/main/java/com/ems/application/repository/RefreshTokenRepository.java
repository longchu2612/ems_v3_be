package com.ems.application.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ems.application.entity.RefreshToken;
import com.ems.application.entity.User;

public interface RefreshTokenRepository extends JpaRepositoryBase<RefreshToken, Integer>,
        JpaSpecificationExecutor<RefreshToken> {

    Optional<RefreshToken> findByRefreshToken(String token);

    List<RefreshToken> findAllByUserIs(User user);

    @Query("SELECT t FROM RefreshToken t WHERE t.user.id = :userId")
    List<RefreshToken> findAllByUserId(@Param("userId") Integer userId);
}
