package com.ems.application.controller.payment;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.payment.PaymentRequest;
import com.ems.application.dto.payment.PaymentResponse;
import com.ems.application.service.payment.PaymentService;

@RestController
@RequestMapping(value = "/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest req) throws UnsupportedEncodingException {
        return paymentService.createPayment(paymentRequest, req);
    }
    // @PostMapping("/ipn")
    // public ResponseEntity<IPNResponse> getPaymentDetail(@Valid @RequestBody
    // IPNRequest ipnRequest,
    // HttpServletRequest req) {
    // return paymentService.getPaymentDetail(req);
    // }
}
