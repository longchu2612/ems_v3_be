package com.ems.application.exception;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ems.application.dto.base.BaseResponse;
import com.ems.application.enums.ErrorMessageType;
import com.ems.application.util.JsonUtils;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            if (e instanceof JwtException || e instanceof AccessDeniedException) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                BaseResponse body = new BaseResponse();
                body.setType(ErrorMessageType.SHOW_IN_TITLE);
                body.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().println(JsonUtils.toJson(body));
            } else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                BaseResponse body = new BaseResponse();
                body.setType(ErrorMessageType.SHOW_IN_TITLE);
                body.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().println(JsonUtils.toJson(body));
                log.info(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
