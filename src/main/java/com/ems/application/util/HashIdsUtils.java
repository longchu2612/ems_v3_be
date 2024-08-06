package com.ems.application.util;

import org.hashids.Hashids;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ems.config.ConstantProperties;

@Component
public class HashIdsUtils {

    private final ConstantProperties constantProperties;

    public HashIdsUtils(ConstantProperties constantProperties) {
        this.constantProperties = constantProperties;
    }

    @Bean
    private Hashids hashIds() {
        return new Hashids(
                constantProperties.getString("hash.secretKey"),
                constantProperties.getInt("hash.tokenLengthMin"),
                constantProperties.getString("hash.number"));
    }

    public String encodeFileName(long... numbers) {
        return hashIds().encode(numbers);
    }

    public long[] decodeFileName(String token) {
        long[] values;
        try {
            values = hashIds().decode(token);
        } catch (Exception e) {
            return null;
        }
        if (values.length > 0) {
            return values;
        }
        return null;
    }

    public String encodeId(long value) {
        return hashIds().encode(value);
    }

    public int decodeId(String token) {
        long[] values;
        try {
            values = hashIds().decode(token);
        } catch (Exception e) {
            return -1;
        }
        if (values.length > 0) {
            return (int) values[0];
        }
        return -1;
    }

    public String encodeString(String value) {
        return hashIds().encodeHex(value);
    }

    public String decodeString(String token) {
        return hashIds().decodeHex(token);
    }
}
