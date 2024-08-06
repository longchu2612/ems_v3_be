package com.ems.application.mapping.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ems.application.dto.user.NewUserRequest;
import com.ems.application.dto.user.UserResponse;
import com.ems.application.entity.User;
import com.ems.application.util.HashIdsUtils;

public class UserMapping {
    public static User convertToEntity(NewUserRequest userRequestRequest, User dtbUser,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        dtbUser.setUserName(userRequestRequest.getUserName());
        dtbUser.setPassword(bCryptPasswordEncoder.encode("123456aA"));
        dtbUser.setPhone(userRequestRequest.getPhone());
        dtbUser.setEmail(userRequestRequest.getEmail());
        dtbUser.setFullName(userRequestRequest.getFullName());
        dtbUser.setAvatar(userRequestRequest.getAvatar());
        dtbUser.setUserType(userRequestRequest.getUserType());
        dtbUser.setIsActive(userRequestRequest.getIsActive());
        dtbUser.setIsLocked(userRequestRequest.getIsLocked());
        dtbUser.setLastLogin(userRequestRequest.getLastLogin());
        dtbUser.setLastFailedAttempt(userRequestRequest.getLastFailedAttempt());
        dtbUser.setFailedAttemptTimes(userRequestRequest.getFailedAttemptTimes());

        return dtbUser;
    }

    public static UserResponse convertToDto(User dtbUser, HashIdsUtils hashIdsUtils) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(dtbUser.getId());
        userResponse.setUserId(hashIdsUtils.encodeId(dtbUser.getId()));
        userResponse.setUserName(dtbUser.getUserName());
        userResponse.setPhone(dtbUser.getPhone());
        userResponse.setEmail(dtbUser.getEmail());
        userResponse.setFullName(dtbUser.getFullName());
        userResponse.setAvatar(dtbUser.getAvatar());
        userResponse.setIsActive(dtbUser.getIsActive());
        userResponse.setIsLocked(dtbUser.getIsLocked());

        return userResponse;
    }
}
