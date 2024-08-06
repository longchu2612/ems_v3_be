package com.ems.application.service.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.ems.application.repository.cache.TokenCacheLogic;
import com.ems.application.util.MessageTranslator;

import io.jsonwebtoken.Claims;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenCacheLogic tokenCacheLogic;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
            TokenCacheLogic tokenCacheLogic, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.tokenCacheLogic = tokenCacheLogic;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
            FilterChain chain) throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken(req);

        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    protected UsernamePasswordAuthenticationToken getAuthentication(String token) {
        if (StringUtils.hasText(token)) {
            Claims claims = jwtTokenProvider.getClaims(token);
            String useName = claims.getSubject();
            List<LinkedHashMap<String, String>> authList = claims.get("AUTH", List.class);
            Collection<GrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
            for (Map<String, String> authMap : authList) {
                for (String authority : authMap.values()) {
                    simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority));
                }
            }

            boolean isExistedToken = tokenCacheLogic.checkToken(token);
            if (isExistedToken) {
                return jwtTokenProvider.getAuthentication(useName, simpleGrantedAuthorities);
            }

            throw new AccessDeniedException(MessageTranslator.toLocale("authentication.invalid"));
        }
        return null;
    }
}
