package com.ems.application.mapping.role;

import com.ems.application.dto.role.NewRoleRequest;
import com.ems.application.dto.role.RoleResponse;
import com.ems.application.entity.Role;
import com.ems.application.util.HashIdsUtils;

public class RoleMapping {
    public static Role convertToEntity(NewRoleRequest roleRequest, Role dtbRole) {
        dtbRole.setRoleName(roleRequest.getName());
        return dtbRole;
    }

    public static RoleResponse convertToDto(Role dtbRole, HashIdsUtils hashIdsUtils) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setId(dtbRole.getId());
        roleResponse.setRoleId(hashIdsUtils.encodeId(dtbRole.getId()));
        roleResponse.setRoleName(dtbRole.getRoleName());
        return roleResponse;
    }

}
