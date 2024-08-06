package com.ems.application.dto.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ems.application.dto.base.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class VerificationTokenResponse extends BaseResponse {

    private String token;
}