package com.ems.application.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPNRequest {
    private String code;
    private String message;
    private String data;
}
