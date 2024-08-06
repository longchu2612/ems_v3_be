package com.ems.application.dto.role;

import com.ems.application.dto.common.SearchCriteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleListSearchCriteria extends SearchCriteria {
    private String createdAt;
}
