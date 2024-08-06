package com.ems.application.service.authentication;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import com.ems.application.dto.authentication.LoginRequest;
import com.ems.application.dto.authentication.LoginResponse;
import com.ems.application.dto.authentication.LoginUserDetail;
import com.ems.application.entity.RefreshToken;
import com.ems.application.entity.User;
import com.ems.application.enums.ErrorMessageType;
import com.ems.application.repository.RefreshTokenRepository;
import com.ems.application.repository.cache.TokenCacheLogic;
import com.ems.application.repository.cache.UserCacheLogic;
import com.ems.application.service.BaseService;
import com.ems.application.util.Constants;
import com.ems.application.util.MessageTranslator;

import io.jsonwebtoken.Claims;

@Service

public class AuthenticationService extends BaseService {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserCacheLogic userCacheLogic;
    private final TokenCacheLogic tokenCacheLogic;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationService(JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService customUserDetailsService,
            BCryptPasswordEncoder bCryptPasswordEncoder, UserCacheLogic userCacheLogic,
            TokenCacheLogic tokenCacheLogic, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userCacheLogic = userCacheLogic;
        this.tokenCacheLogic = tokenCacheLogic;
        this.refreshTokenRepository = refreshTokenRepository;

    }

    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)
            throws BadCredentialsException {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        LoginResponse loginResponse;
        try {
            // Check user existence
            LoginUserDetail userDetails = customUserDetailsService.loadUserByUsername(username);
            if (userDetails != null
                    && userDetails.getUsername().equalsIgnoreCase(username)) {
                if (!bCryptPasswordEncoder.matches(
                        password, userDetails.getPassword())) {
                    // Logic failed login
                    loginResponse = failedAttempt(username);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
                }
                HttpHeaders responseHeaders = new HttpHeaders();
                loginResponse = successfulAttempt(username, userDetails, responseHeaders);
                if (!StringUtils.hasText(loginResponse.getTitle()))
                    return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }
            loginResponse = getUnauthorizedResponse();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (Exception exception) {
            throw new BadCredentialsException(exception.getMessage());
        }
    }

    public ResponseEntity<String> logout(String tokenValue) {
        if (StringUtils.hasText(tokenValue)) {
            // parse the token.
            String token = tokenValue.replace(Constants.BEARER_STRING, "");
            tokenCacheLogic.removeToken(token);
        }
        return ResponseEntity.ok().body(MessageTranslator.toLocale("logout.success"));
    }

    private LoginResponse failedAttempt(String userName) {

        User user = userCacheLogic.getUserByUsername(userName);

        int failedAttemptTimes = user.getFailedAttemptTimes();
        if (!user.getIsLocked())
            failedAttemptTimes++;
        if (failedAttemptTimes >= 3)
            user.setIsLocked(true);

        LocalDateTime lastFailedAttempt = LocalDateTime.now();

        user.setFailedAttemptTimes(failedAttemptTimes);
        user.setLastFailedAttempt(lastFailedAttempt);
        // Update user
        userCacheLogic.updateUser(user);

        LoginResponse response = new LoginResponse();
        response.setType(ErrorMessageType.SHOW_IN_TITLE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        if (failedAttemptTimes < 3)
            response.setTitle(MessageTranslator.toLocale("validation.loginFailed"));
        else
            response.setTitle(MessageTranslator.toLocale("validation.userLocked"));
        return response;
    }

    private LoginResponse successfulAttempt(String username, LoginUserDetail userDetails, HttpHeaders responseHeaders) {
        LoginResponse loginResponse;
        User user = userCacheLogic.getUserByUsername(username);
        if (!isUserLocked(user)) {
            // token
            String token = jwtTokenProvider.createToken(username, userDetails.getAuthorities());
            String refreshToken = jwtTokenProvider.createRefreshToken(username, user.getRoles());
            // Remove user token
            tokenCacheLogic.deleteToken(user);
            tokenCacheLogic.saveToken(userDetails.getId(), token, userDetails.getDeviceToken(),
                    userDetails.getDeviceType());
            saveRefreshToken(refreshToken, user);

            // Set role
            /*
             * Optional<DtbRttRole> roleOptional = roleCacheLogic.getRoleByName(
             * user.getRoles().get(0).getName().getValue());
             * if (roleOptional.isPresent()) {
             * Role role = roleOptional.get();
             * loginResponse.setRole(role.getName());
             * loginResponse.setRoleResponse(roleMapper.toDto(role));
             * }
             */

            responseHeaders.set(Constants.AUTHORIZATION_STRING, token);
            responseHeaders.set(Constants.REFRESH_TOKEN_STRING, refreshToken);

            // Set response
            loginResponse = new LoginResponse();
            loginResponse.setAvatar(user.getAvatar());
            loginResponse.setFullName(user.getFullName());
            loginResponse.setPhoneNumber(user.getPhone());
            loginResponse.setRole(user.getRoles().get(0).getRoleName());
            if (Objects.isNull(user.getLastLogin()))
                loginResponse.setFirstLogin(true);

            // update user
            user.setIsLocked(false);
            user.setLastLogin(LocalDateTime.now());
            user.setLastFailedAttempt(null);
            user.setFailedAttemptTimes(0);
            userCacheLogic.updateUser(user);
        } else {
            userCacheLogic.removeGetUserByUsername(username);
            loginResponse = new LoginResponse();
            loginResponse.setType(ErrorMessageType.SHOW_IN_TITLE);
            loginResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            loginResponse.setTitle(MessageTranslator.toLocale("validation.userLocked"));
        }
        return loginResponse;
    }

    private boolean isUserLocked(User user) {
        if (Objects.isNull(user.getIsLocked()))
            return false;
        if (user.getIsLocked()) {
            return !user.getLastFailedAttempt().isBefore(LocalDateTime.now().minusMinutes(2));
        }
        return false;
    }

    private void saveRefreshToken(String tokenString, User user) {
        RefreshToken token = new RefreshToken();
        token.setRefreshToken(tokenString);
        token.setUser(user);
        refreshTokenRepository.save(token);
    }

    private LoginResponse getUnauthorizedResponse() {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setType(ErrorMessageType.SHOW_IN_TITLE);
        loginResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        loginResponse.setTitle(MessageTranslator.toLocale("validation.loginFailed"));

        return loginResponse;
    }

    public ResponseEntity<LoginResponse> pushRefreshToken(String pushToken) {

        Claims claims = jwtTokenProvider.getClaims(pushToken);
        String username = claims.getSubject();
        LoginResponse loginResponse;
        try {
            // Check user existence
            LoginUserDetail userDetails = customUserDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                // Logic success login
                // if (userDetails.getRole().equals(RoleType.ROLE_WAITER.getRole())) {
                // userDetails.setDeviceToken(loginRequest.getDeviceToken());
                // userDetails.setDeviceType(loginRequest.getDeviceType());
                // }
                HttpHeaders responseHeaders = new HttpHeaders();
                loginResponse = successfulAttempt(username, userDetails, responseHeaders);
                if (!StringUtils.hasText(loginResponse.getTitle()))
                    return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
            }
            loginResponse = getUnauthorizedResponse();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (Exception exception) {
            throw new BadCredentialsException(exception.getMessage());
        }
    }
}