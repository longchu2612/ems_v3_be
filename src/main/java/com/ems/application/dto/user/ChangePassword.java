package com.ems.application.dto.user;

import lombok.Data;

@Data
public class ChangePassword {
    private String username;
    private String newPassword;
    private String oldPassword;
}
