package com.ems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.ems.application.exception.NotFoundException;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRetry
class RetryConfig {

    @Bean
    RetryTemplate retryTemplate(
            @Value("${endpoints.retry.interval}") Long interval,
            @Value("${endpoints.retry.multiplier}") Long multiplier,
            @Value("${endpoints.retry.maxAttemps}") Integer maxAttemps) {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(interval);
        backOffPolicy.setMultiplier(multiplier);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        Map<Class<? extends Throwable>, Boolean> retryExceptions = new HashMap<>();
        retryExceptions.put(NotFoundException.class, false);
        retryExceptions.put(SocketTimeoutException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttemps, retryExceptions, true, false);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
}
