package com.ems.application.service.authentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ems.application.dto.authentication.LoginResponse;
import com.ems.application.dto.base.BaseResponse;
import com.ems.application.dto.verification.ForgetPasswordRequest;
import com.ems.application.dto.verification.TokenRequest;
import com.ems.application.dto.verification.VerificationTokenResponse;
import com.ems.application.entity.User;
import com.ems.application.entity.VerificationToken;
import com.ems.application.enums.ErrorMessageType;
import com.ems.application.repository.VerificationTokenRepository;
import com.ems.application.repository.cache.TokenCacheLogic;
import com.ems.application.repository.cache.UserCacheLogic;
import com.ems.application.service.BaseService;
import com.ems.application.util.HashIdsUtils;
import com.ems.application.util.MessageTranslator;

@Service

public class ResetPasswordService extends BaseService {

    private final UserCacheLogic userCacheLogic;
    private final HashIdsUtils hashIdsUtils;
    private final VerificationTokenRepository verificationTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenCacheLogic tokenCacheLogic;

    public ResetPasswordService(UserCacheLogic userCacheLogic, HashIdsUtils hashIdsUtils,
            VerificationTokenRepository verificationTokenRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder, TokenCacheLogic tokenCacheLogic) {
        this.userCacheLogic = userCacheLogic;
        this.hashIdsUtils = hashIdsUtils;
        this.verificationTokenRepository = verificationTokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenCacheLogic = tokenCacheLogic;
    }

    public ResponseEntity<VerificationTokenResponse> verifyUser(String username) {
        // Verify user
        User user = userCacheLogic.getUserByUsername(username);
        if (Objects.isNull(user)) {
            return ResponseEntity.notFound().build();
        }

        // Generate OTP
        LocalDateTime expiredTime = LocalDateTime.now().plusMinutes(3);
        String token = hashIdsUtils.encodeId(expiredTime.toEpochSecond(ZoneOffset.UTC)) + "."
                + hashIdsUtils.encodeId(user.getId());

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiredTime(expiredTime);
        verificationTokenRepository.save(verificationToken);

        VerificationTokenResponse response = new VerificationTokenResponse();
        response.setToken(token);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<BaseResponse> verifyToken(TokenRequest tokenRequest) {
        // Check expired time
        String[] tokenArray = tokenRequest.getToken().split("\\.");
        if (tokenArray.length != 3) {
            ResponseEntity.badRequest().body("");
        }
        String expiredTimeEncode = tokenArray[0];
        int userId = hashIdsUtils.decodeId(tokenArray[1]);
        String otp = hashIdsUtils.decodeId(tokenArray[2]) > 0 ? String.valueOf(
                hashIdsUtils.decodeId(tokenArray[2]))
                : "";
        long expiredTime = hashIdsUtils.decodeId(expiredTimeEncode);

        return validateToken(tokenRequest.getToken(), expiredTime, otp, userId, tokenRequest.getOtp());
    }

    public ResponseEntity<BaseResponse> resetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        // Check expired time
        String[] tokenArray = forgetPasswordRequest.getToken().split("\\.");
        if (tokenArray.length != 3) {
            ResponseEntity.badRequest().body("");
        }
        String expiredTimeEncode = tokenArray[0];
        int userId = hashIdsUtils.decodeId(tokenArray[1]);
        String otp = hashIdsUtils.decodeId(tokenArray[2]) > 0 ? String.valueOf(
                hashIdsUtils.decodeId(tokenArray[2]))
                : "";
        long expiredTime = hashIdsUtils.decodeId(expiredTimeEncode);
        ResponseEntity<BaseResponse> validationResponse = validateToken(
                forgetPasswordRequest.getToken(),
                expiredTime, otp, userId, forgetPasswordRequest.getOtp());
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }
        // Update user
        User user = userCacheLogic.getUserById(userId);
        user.setPassword(bCryptPasswordEncoder.encode(forgetPasswordRequest.getNewPassword()));
        userCacheLogic.updateUser(user);
        // Remove user token
        tokenCacheLogic.deleteToken(user);
        // Remove token
        verificationTokenRepository.deleteByToken(forgetPasswordRequest.getToken());

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<BaseResponse> validateToken(String token, long expiredTime, String otp,
            Integer userId, String otpRequest) {
        // check token expire
        if (expiredTime <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.otp.invalid")));
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(expiredTime),
                ZoneOffset.UTC);
        if (localDateTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.token.expired")));
        }
        // check otp
        if (!otpRequest.equals(otp)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.otp.invalid")));
        }
        // Verify user
        if (userId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.token.invalid")));
        }
        // Check existence token
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(
                token);
        if (!tokenOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.token.invalid")));
        }

        User user = userCacheLogic.getUserById(userId);
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(setErrorResponse(MessageTranslator.toLocale("exception.notfound.user")));
        }

        return ResponseEntity.ok().build();
    }

    private BaseResponse setErrorResponse(String message) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setType(ErrorMessageType.SHOW_IN_TITLE);
        loginResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        loginResponse.setTitle(message);

        return loginResponse;
    }
}
