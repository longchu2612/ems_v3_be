package com.ems.application.controller.role;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.role.NewRoleRequest;
import com.ems.application.dto.role.RoleListSearchCriteria;
import com.ems.application.dto.role.RoleResponse;
import com.ems.application.service.role.RoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Role")

@RestController
@RequestMapping(value = "/api/role")
public class RoleController {
    private final RoleService roleRttService;

    public RoleController(RoleService roleRttService) {
        this.roleRttService = roleRttService;
    }

    @ApiOperation(value = "Add new role")
    @PostMapping(value = "/add")
    public ResponseEntity<RoleResponse> addRole(@Valid @RequestBody NewRoleRequest roleRequest) {
        return roleRttService.createNewRole(roleRequest);
    }

    @ApiOperation(value = "Get list role ")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<RoleResponse>> getIdSites(
            @Valid @RequestBody RoleListSearchCriteria criteria) {
        return roleRttService.getAllRoles(criteria);
    }

    @ApiOperation(value = "Update role")
    @PostMapping(value = "/update/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable("id") int id,
            @Valid @RequestBody NewRoleRequest roleRequest) {
        return roleRttService.updateRole(id, roleRequest);
    }

    @ApiOperation(value = "Get role by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable("id") int id) {
        return roleRttService.getRoleById(id);
    }

    @ApiOperation(value = "Delete a role")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<RoleResponse> deleteRoleById(@PathVariable("id") int id) {
        return roleRttService.deleteRoleById(id);
    }

}
