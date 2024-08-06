package com.ems.application.controller.verification;

import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.base.BaseResponse;
import com.ems.application.dto.verification.ForgetPasswordRequest;
import com.ems.application.dto.verification.TokenRequest;
import com.ems.application.dto.verification.VerificationTokenResponse;
import com.ems.application.service.authentication.ResetPasswordService;

@Api(tags = "Reset password")
@RestController
@RequestMapping("/api/auth")
public class VerificationController {

    private final ResetPasswordService resetPasswordService;

    public VerificationController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @GetMapping("/verify-user/{username}")
    public ResponseEntity<VerificationTokenResponse> verifyUser(
            @PathVariable("username") String username) {
        return resetPasswordService.verifyUser(username);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<BaseResponse> verifyToken(@Valid @RequestBody TokenRequest tokenRequest) {
        return resetPasswordService.verifyToken(tokenRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse> resetPassword(
            @Valid @RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        return resetPasswordService.resetPassword(forgetPasswordRequest);
    }
}
