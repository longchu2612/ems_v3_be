package com.ems.application.dto.user;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NewUserRequest {

    private String userName;
    private String phone;
    private String email;
    private String fullName;
    private String avatar;
    private Integer userType;
    private Integer roleId;
    private Boolean isActive = true;
    private Boolean isLocked = false;
    private LocalDateTime lastLogin;
    private LocalDateTime lastFailedAttempt;
    private Integer failedAttemptTimes = 0;
    private Integer branchId;
    private String branchIdString;
}
