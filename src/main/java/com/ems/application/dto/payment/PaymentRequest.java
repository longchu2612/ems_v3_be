package com.ems.application.dto.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String bankCode;
    private String orderId;
    private long amount;
    private String language;
}
