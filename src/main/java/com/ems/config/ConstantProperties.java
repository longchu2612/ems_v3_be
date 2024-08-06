package com.ems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:constant.properties")
@ConfigurationProperties()
public class ConstantProperties {
    private Map<String, String> config;

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getString(String key) {
        return config.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(config.get(key));
    }

}
