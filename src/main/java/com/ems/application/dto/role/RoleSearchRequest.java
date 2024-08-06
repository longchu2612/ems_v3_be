package com.ems.application.dto.role;

import com.ems.application.dto.common.PaginationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSearchRequest extends PaginationRequest {

    private String name;
}
