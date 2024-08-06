package com.ems.application.controller.authentication;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.authentication.LoginRequest;
import com.ems.application.dto.authentication.LoginResponse;
import com.ems.application.service.authentication.AuthenticationService;
import com.ems.application.util.Constants;

import io.swagger.annotations.Api;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @CrossOrigin(exposedHeaders = { Constants.AUTHORIZATION_STRING, Constants.REFRESH_TOKEN_STRING })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
            throws BadCredentialsException {
        return authenticationService.login(loginRequest);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(Constants.AUTHORIZATION_STRING) String token) {
        return authenticationService.logout(token);
    }

    // @CrossOrigin(exposedHeaders = {Constants.AUTHORIZATION_STRING,
    // Constants.REFRESH_TOKEN_STRING})
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> pushRefreshToken(
            @RequestHeader(Constants.REFRESH_TOKEN_STRING) String refreshToken) {
        return authenticationService.pushRefreshToken(refreshToken);
    }
}
