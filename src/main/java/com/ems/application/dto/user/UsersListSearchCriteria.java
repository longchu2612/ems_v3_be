package com.ems.application.dto.user;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersListSearchCriteria extends SearchCriteria{
    private String roleName;
    private String userName;
    private String createdAt;
}
