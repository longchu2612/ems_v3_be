package com.ems.application.dto.authentication;

import com.ems.application.dto.base.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class LoginResponse extends BaseResponse {

    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String role;
    private String eateryId;
    private Boolean firstLogin = false;
}
