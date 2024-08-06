package com.ems.application.service.authentication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ems.application.dto.authentication.LoginUserDetail;
import com.ems.application.entity.Role;
import com.ems.application.entity.User;
import com.ems.application.repository.cache.UserCacheLogic;
import com.ems.application.service.BaseService;

@Component
public class CustomUserDetailsService extends BaseService implements UserDetailsService {

    private final UserCacheLogic userCacheLogic;

    public CustomUserDetailsService(UserCacheLogic userCacheLogic) {
        this.userCacheLogic = userCacheLogic;
    }

    @Override
    public LoginUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userCacheLogic.getUserByUsername(username);
        if (Objects.nonNull(user)) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } else {
            return null;
        }
    }

    public User findUserByUsername(String username) {
        return userCacheLogic.getUserByUsername(username);
    }

    private List<GrantedAuthority> getUserAuthority(List<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach(role -> roles.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName())));

        return new ArrayList<>(roles);
    }

    private LoginUserDetail buildUserForAuthentication(User user,
            List<GrantedAuthority> authorities) {
        return new LoginUserDetail(user, authorities);
    }
}
