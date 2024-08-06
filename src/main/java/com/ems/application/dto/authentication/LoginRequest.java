package com.ems.application.dto.authentication;

import com.ems.application.validator.FieldRequired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @FieldRequired(name = "field.username")
    private String username;
    @FieldRequired(name = "field.password")
    private String password;
    private String deviceToken;
    private Integer deviceType;
}
