package com.ems.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder rb) {
        RestTemplate template = rb.build();
        template.setRequestFactory(getNonStreamingBufferedRequestFactory());
        return template;
    }

    private static ClientHttpRequestFactory getNonStreamingBufferedRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return new BufferingClientHttpRequestFactory(requestFactory);
    }
}
