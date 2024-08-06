package com.ems.application.repository.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.ems.application.entity.Role;
import com.ems.application.repository.RoleRepository;

@Component
public class RoleCacheLogic {

    private final RoleRepository roleRepository;

    public RoleCacheLogic(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Cacheable(cacheNames = "getRoleByName", key = "#name", unless = "#result == null")
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByRoleName(name);
    }

    @Cacheable(cacheNames = "getRoleById", key = "#id", unless = "#result == null")
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id).filter(dt -> !dt.isDeleted());
    }

    @Cacheable(cacheNames = "getRoles", unless = "#result == null")
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @CacheEvict(value = "getRoleByName", key = "#name")
    public void clearGetRoleByName(String name) {
    }

    @CacheEvict(value = "getRoleById", key = "#id")
    public void clearGetRoleById(String id) {
    }

    @CacheEvict(value = "getRoles")
    public void clearGetRoles() {
    }
}
