package com.ems.application.dto.user;

import com.ems.application.dto.common.PaginationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequest extends PaginationRequest {

    private String userName;
    private Integer roleId;
    private Boolean deleted = false;
}
