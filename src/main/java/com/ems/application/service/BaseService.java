package com.ems.application.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ems.application.entity.User;
import com.ems.application.exception.NotFoundException;
import com.ems.application.util.MessageTranslator;

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public abstract class BaseService {

    protected BaseService() {
    }

    protected User getUser() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new NotFoundException(MessageTranslator.toLocale("authentication.invalid"));
        }
        return (User) auth.getPrincipal();
    }

    public Pageable createPageRequest(int pageNumber, int recordNumber) {
        return PageRequest.of(Math.max(pageNumber, 0), Math.max(recordNumber, 0));
    }

    public Pageable createPageRequest(int pageNumber, int recordNumber, Sort sort) {
        return PageRequest.of(Math.max(pageNumber, 0), Math.max(recordNumber, 0), sort);
    }

    public Pageable createPageRequest(int pageNumber, int recordNumber, List<Sort.Order> orders) {
        return PageRequest.of(pageNumber, recordNumber, Sort.by(orders));
    }
}
