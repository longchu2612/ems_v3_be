package com.ems.application.util;

import java.util.Random;

public class OtpUtils {

    private static final Random rnd = new Random();

    public static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for (int i = 0; i < digCount; i++) {
            sb.append((char) ('0' + rnd.nextInt(9) + 1));
        }
        return sb.toString();
    }

    public static String generateOtp(boolean isSmsAvailable) {
        String otp;
        if (isSmsAvailable) { // if sms service is available
            otp = getRandomNumber(6);
        } else {
            otp = "112233";
        }
        return otp;
    }
}
