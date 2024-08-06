package com.ems.application.service.authentication;

import static com.ems.application.util.Constants.AUTHORIZATION_STRING;
import static com.ems.application.util.Constants.BEARER_STRING;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.ems.application.entity.Role;
import com.ems.application.entity.User;
import com.ems.application.util.MessageTranslator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-length:604800000}")
    private long accessTokenLength = 604800000; // 1d

    @Value("${security.jwt.token.expire-refresh-token:604800000}")
    private long refreshTokenLength = 604800000; // 1d

    private final CustomUserDetailsService userDetailsService;

    public JwtTokenProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, Collection<GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("AUTH", authorities);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String username, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        List<String> mappedRoles = roles.stream().map(Role::getRoleName).collect(Collectors.toList());
        claims.put("roles", mappedRoles);
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(AUTHORIZATION_STRING);
        if (bearerToken != null && bearerToken.startsWith(BEARER_STRING)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException(MessageTranslator.toLocale("authentication.invalid"));
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String username,
            Collection<GrantedAuthority> roles) {
        User user = userDetailsService.findUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new AccessDeniedException(MessageTranslator.toLocale("authentication.invalid"));
        }
        return new UsernamePasswordAuthenticationToken(user, null, roles);
    }
}
