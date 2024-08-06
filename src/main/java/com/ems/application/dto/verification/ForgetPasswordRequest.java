package com.ems.application.dto.verification;

import com.ems.application.util.Constants;
import com.ems.application.validator.FieldPattern;
import com.ems.application.validator.FieldRequired;
import com.ems.application.validator.FieldsValueMatch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldsValueMatch(field = "newPassword", fieldMatch = "confirmNewPassword", message = "validation.password.notMatch")
public class ForgetPasswordRequest {

    @FieldRequired(name = "field.newPassword")
    @FieldPattern(name = "field.newPassword", regexp = Constants.PASSWORD_REGEX, message = "validation.password.invalid")
    private String newPassword;
    @FieldRequired(name = "field.confirmPassword")
    private String confirmNewPassword;
    @FieldRequired(name = "field.otp")
    private String otp;
    @FieldRequired(name = "field.token")
    private String token;
}
