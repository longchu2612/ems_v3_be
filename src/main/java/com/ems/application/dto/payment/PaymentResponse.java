package com.ems.application.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private String code;
    private String message;
    private String data;
}
