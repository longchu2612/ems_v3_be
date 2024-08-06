package com.ems.application.dto.verification;

import com.ems.application.validator.FieldRequired;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    @FieldRequired(name = "field.otp")
    private String otp;

    @FieldRequired(name = "field.token")
    private String token;

}
