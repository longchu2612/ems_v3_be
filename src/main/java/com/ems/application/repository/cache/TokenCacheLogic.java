package com.ems.application.repository.cache;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ems.application.entity.Token;
import com.ems.application.entity.User;
import com.ems.application.repository.TokenRepository;
import com.ems.application.repository.UserRepository;

@Component
public class TokenCacheLogic {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final CacheManager cacheManager;

    public TokenCacheLogic(UserRepository userRepository, TokenRepository tokenRepository,
            CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = "checkToken", key = "#token")
    public boolean checkToken(String token) {
        List<Token> tokens = tokenRepository.findByTokenAndIsDeletedFalse(token);
        return !tokens.isEmpty();
    }

    @CacheEvict(cacheNames = "checkToken", key = "#token")
    public void removeToken(String token) {
        Optional<Token> userTokenOptional = tokenRepository.findByToken(token);
        userTokenOptional.ifPresent(userToken -> tokenRepository.deleteById(userToken.getId()));
    }

    public void saveToken(Integer userId, String tokenString, String deviceToken,
            Integer deviceType) {
        Token token = new Token();
        token.setToken(tokenString);
        if (Objects.nonNull(deviceToken)) {
            token.setDeviceToken(deviceToken);
        }
        if (Objects.nonNull(deviceType)) {
            token.setDeviceType(deviceType);
        }
        Optional<User> userOpt = userRepository.findById(userId);
        userOpt.ifPresent(token::setUser);
        tokenRepository.save(token);
    }

    public void deleteToken(User user) {
        List<Token> tokens = tokenRepository.findAllByUserId(user.getId());
        if (!CollectionUtils.isEmpty(tokens)) {
            tokens.forEach(dtbToken -> Objects.requireNonNull(cacheManager.getCache("checkToken"))
                    .evict(dtbToken.getToken()));
        }
        List<Integer> tokenIds = tokens.stream().map(Token::getId).collect(Collectors.toList());
        tokenRepository.deleteAllByIdInBatch(tokenIds);
    }
}
