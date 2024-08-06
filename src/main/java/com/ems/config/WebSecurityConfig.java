package com.ems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ems.application.exception.ExceptionHandlerFilter;
import com.ems.application.repository.cache.TokenCacheLogic;
import com.ems.application.service.authentication.JwtAuthorizationFilter;
import com.ems.application.service.authentication.JwtTokenProvider;
import com.ems.application.util.Constants;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${app.cors.allowedOrigins:*}")
    private String[] corsAllowedOrigins;

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenCacheLogic tokenCacheLogic;

    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider, TokenCacheLogic tokenCacheLogic) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenCacheLogic = tokenCacheLogic;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors(cors -> cors
                .configurationSource(request -> corsConfiguration().applyPermitDefaultValues()))
                .csrf(csrf -> csrf.disable()).formLogin(login -> login.disable());
        http.authorizeRequests(requests -> requests
                .antMatchers("/api/auth/**",
                        "/api/external-driver/**",
                        "/api/file/download/**",
                        "/api/products/list",
                        "/api/categories/list",
                        "/api/orders/**",
                        "/api/payments/**",
                        "/api/eateries/**",
                        "/api/order-detail/**",
                        "/api/eateries/detail",
                        "/api/notification/**",
                        "/api/tables/list")
                .permitAll())
                .authorizeRequests(requests -> requests
                        .antMatchers("/api/**")
                        .authenticated());
        // Set permissions on endpoints
        http.addFilterBefore(new ExceptionHandlerFilter(), JwtAuthorizationFilter.class);
        // JWT token filter
        http.addFilter(new JwtAuthorizationFilter(authenticationManager(), tokenCacheLogic,
                jwtTokenProvider));

        // Set session management to stateless
        http.sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/v2/api-docs/**")
                .antMatchers("/swagger-ui/**")
                .antMatchers("/swagger.json")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources/**");
    }

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addExposedHeader(Constants.AUTHORIZATION_STRING);
        corsConfiguration.addExposedHeader(Constants.REFRESH_TOKEN_STRING);
        for (String allowed : corsAllowedOrigins) {
            corsConfiguration.addAllowedOriginPattern(allowed);
        }
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
        corsConfiguration.addAllowedMethod(HttpMethod.GET);
        corsConfiguration.addAllowedMethod(HttpMethod.POST);
        corsConfiguration.addAllowedMethod(HttpMethod.PUT);
        corsConfiguration.addAllowedMethod(HttpMethod.PATCH);
        corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);

        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", corsConfiguration);

        return corsConfiguration;
    }
}
