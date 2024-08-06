package com.ems.application.repository.cache;

import java.util.Objects;
import java.util.Optional;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.ems.application.entity.User;
import com.ems.application.repository.UserRepository;

@Component
public class UserCacheLogic {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public UserCacheLogic(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Cacheable(cacheNames = "getUserByUsername", key = "#username", unless = "#result == null")
    public User getUserByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUserNameAndIsDeletedFalse(username);
        return userOpt.orElse(null);
    }

    @Cacheable(cacheNames = "getUserById", key = "#id", unless = "#result == null")
    public User getUserById(int id) {
        Optional<User> userOpt = userRepository.findById(id).filter(dt -> !dt.isDeleted());
        return userOpt.orElse(null);
    }

    public void removeGetUserById(int id) {
        Objects.requireNonNull(cacheManager.getCache("getUserById")).evict(id);
    }

    public void removeGetUserByUsername(String username) {
        Objects.requireNonNull(cacheManager.getCache("getUserByUsername")).evict(username);
    }

    public void updateUser(User user) {
        userRepository.save(user);

        // clear cache
        removeGetUserById(user.getId());
        removeGetUserByUsername(user.getUserName());
    }
}
