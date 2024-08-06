package com.ems.application.dto.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilesUserResponse {
    private Integer id;
    private String userName;
    private String phone;
    private String email;
    private String fullName;
    private String avatar;
    private String userType;
    private Integer branchId;
}
