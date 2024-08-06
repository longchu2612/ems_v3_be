package com.ems.application.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Integer id;
    private String userId;
    private String userName;
    private String phone;
    private String email;
    private String fullName;
    private String avatar;
    private Boolean isActive = true;
    private Boolean isLocked = false;
    private Integer branchId;
    private String branchIdString;
    private String roleName;
}
