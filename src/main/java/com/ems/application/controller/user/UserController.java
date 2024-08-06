package com.ems.application.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.user.ChangePassword;
import com.ems.application.dto.user.NewUserRequest;
import com.ems.application.dto.user.UserListSearchCriteria;
import com.ems.application.dto.user.UserResponse;
import com.ems.application.service.authentication.JwtTokenProvider;
import com.ems.application.service.user.UserService;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "User")

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userRttService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userRttService, JwtTokenProvider jwtTokenProvider) {
        this.userRttService = userRttService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiOperation(value = "Add new user")
    @PostMapping(value = "/add")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userRttService.createNewUser(userRequest);
    }

    @ApiOperation(value = "Get list user ")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @Valid @RequestBody UserListSearchCriteria criteria) {
        return userRttService.getAllUser(criteria);
    }

    @ApiOperation(value = "Update user")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") String id,
            @Valid @RequestBody NewUserRequest userRequest) {
        return userRttService.updateUser(id, userRequest);
    }

    @ApiOperation(value = "Update user")
    @PutMapping(value = "/update")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody NewUserRequest userRequest, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Claims claims = jwtTokenProvider.getClaims(token);
        userRequest.setUserName(claims.getSubject());
        return userRttService.updateProfile(userRequest);
    }

    @ApiOperation(value = "Change-password user")
    @PostMapping(value = "/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @Valid @RequestBody ChangePassword changePassword,
            HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Claims claims = jwtTokenProvider.getClaims(token);
        changePassword.setUsername(claims.getSubject());
        return userRttService.changePassword(changePassword);
    }

    @ApiOperation(value = "Unlock user")
    @PostMapping(value = "/unlock/{id}")
    public ResponseEntity<UserResponse> unlockUser(@PathVariable("id") String id) {
        return userRttService.unLockUser(id);
    }

    @ApiOperation(value = "Get user by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") String id) {
        return userRttService.getUserById(id);
    }

    @ApiOperation(value = "Get user detail")
    @GetMapping(value = "/detail")
    public ResponseEntity<UserResponse> getUserDetail(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Claims claims = jwtTokenProvider.getClaims(token);
        return userRttService.getUserDetail(claims.getSubject());
    }

    //
    @ApiOperation(value = "Delete a user")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<UserResponse> deleteUserById(@PathVariable("id") String id) {
        return userRttService.deleteUserById(id);
    }

    @ApiOperation(value = "Reset password")
    @PutMapping(value = "/reset-password/{id}")
    public ResponseEntity<UserResponse> resetPassword(@PathVariable("id") String id) {
        return userRttService.resetPassword(id);
    }

}
