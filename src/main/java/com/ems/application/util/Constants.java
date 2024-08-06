package com.ems.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String AUTHORIZATION_STRING = "Authorization";
    public static final String REFRESH_TOKEN_STRING = "RefreshToken";
    public static final String BEARER_STRING = "Bearer ";
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9@#$%^&]{6,}$";
}
