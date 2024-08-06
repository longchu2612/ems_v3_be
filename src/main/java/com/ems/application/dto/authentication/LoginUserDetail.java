package com.ems.application.dto.authentication;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import com.ems.application.entity.User;

@Getter
@Setter
public class LoginUserDetail extends org.springframework.security.core.userdetails.User {

    private Integer id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private Collection<GrantedAuthority> authorities;
    private boolean deleted;
    private String deviceToken;
    private Integer deviceType;
    private LocalDateTime lastLogin;

    public LoginUserDetail(User user, Collection<GrantedAuthority> authorities) {
        super(user.getUserName(), Objects.nonNull(user.getPassword()) ? user.getPassword() : "",
                authorities);
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhone();
        this.fullName = user.getFullName();
        this.role = user.getRoles().get(0).getRoleName();
        this.authorities = authorities;
        this.deleted = user.isDeleted();
        this.lastLogin = user.getLastLogin();
    }
}
