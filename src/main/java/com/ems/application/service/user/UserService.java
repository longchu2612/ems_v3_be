package com.ems.application.service.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ems.application.dto.user.ChangePassword;
import com.ems.application.dto.user.NewUserRequest;
import com.ems.application.dto.user.UserListSearchCriteria;
import com.ems.application.dto.user.UserResponse;
import com.ems.application.entity.Role;
import com.ems.application.entity.User;
import com.ems.application.entity.UserRole;
import com.ems.application.exception.NotFoundException;
import com.ems.application.mapping.user.UserMapping;
import com.ems.application.repository.RoleRepository;
import com.ems.application.repository.UserRepository;
import com.ems.application.repository.UserRoleRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class UserService extends BaseService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final HashIdsUtils hashIdsUtils;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository,
            RoleRepository roleRepository, HashIdsUtils hashIdsUtils, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.hashIdsUtils = hashIdsUtils;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<UserResponse> createNewUser(NewUserRequest userRequest) {
        User dtbUserEntity = userRepository.findByUserNameAndIsDeletedFalse(userRequest.getUserName()).orElse(null);
        if (dtbUserEntity != null) {
            throw new NotFoundException("User already exists");
        }
        dtbUserEntity = new User();
        User dtbUser = UserMapping.convertToEntity(userRequest, dtbUserEntity, bCryptPasswordEncoder);
        User userResponse = userRepository.save(dtbUser);
        UserRole userRole = new UserRole();
        userRole.setUserId(userResponse.getId());
        userRole.setRoleId(userRequest.getRoleId());
        userRoleRepository.save(userRole);
        Role role = roleRepository.getById(userRequest.getRoleId());
        UserResponse response = UserMapping.convertToDto(userResponse, hashIdsUtils);
        response.setRoleName(role.getRoleName());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<UserResponse> updateUser(String id, NewUserRequest userRequest) {
        Integer userId = hashIdsUtils.decodeId(id);
        Optional<User> user = userRepository.findById(userId).filter(dt -> !dt.isDeleted());
        if (user.isPresent()) {
            User dtbUserEntity = user.get();
            User dtbUser = UserMapping.convertToEntity(userRequest, dtbUserEntity, bCryptPasswordEncoder);
            User userResponse = userRepository.save(dtbUser);
            UserRole userRole = userRoleRepository.findByUserId(userResponse.getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            userRole.setRoleId(userRequest.getRoleId());
            userRoleRepository.save(userRole);
            UserResponse response = UserMapping.convertToDto(userResponse, hashIdsUtils);
            response.setRoleName(dtbRole.getRoleName());
            return ResponseEntity.ok(response);
        }
        throw new NotFoundException("User not found");
    }

    public ResponseEntity<UserResponse> updateProfile(NewUserRequest userRequest) {
        Optional<User> user = userRepository.findByUserNameAndIsDeletedFalse(userRequest.getUserName());
        if (user.isPresent()) {
            User dtbUserEntity = user.get();
            User dtbUser = UserMapping.convertToEntity(userRequest, dtbUserEntity, bCryptPasswordEncoder);
            User userResponse = userRepository.save(dtbUser);
            UserRole userRole = userRoleRepository.findByUserId(userResponse.getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            UserResponse response = UserMapping.convertToDto(user.get(), hashIdsUtils);
            response.setRoleName(dtbRole.getRoleName());
            return ResponseEntity.ok(response);
        }
        throw new NotFoundException("User not found");
    }

    public ResponseEntity<UserResponse> getUserById(String id) {
        Integer userId = hashIdsUtils.decodeId(id);
        Optional<User> user = userRepository.findById(userId).filter(dt -> !dt.isDeleted());
        if (user.isPresent()) {
            UserRole userRole = userRoleRepository.findByUserId(user.get().getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            UserResponse userResponse = UserMapping.convertToDto(user.get(), hashIdsUtils);
            userResponse.setRoleName(dtbRole.getRoleName());
            return ResponseEntity.ok(userResponse);
        }
        throw new NotFoundException("User not found");
    }

    public ResponseEntity<UserResponse> getUserDetail(String username) {
        Optional<User> user = userRepository.findByUserNameAndIsDeletedFalse(username);
        if (user.isPresent()) {
            UserRole userRole = userRoleRepository.findByUserId(user.get().getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            UserResponse userResponse = UserMapping.convertToDto(user.get(), hashIdsUtils);
            userResponse.setRoleName(dtbRole.getRoleName());
            return ResponseEntity.ok(userResponse);
        }
        throw new NotFoundException("User not found");
    }

    public ResponseEntity<Page<UserResponse>> getAllUser(UserListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));

        Page<User> userPages = userRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setUserListPredicate(root, criteria, builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userPages.getContent()) {
            UserRole userRole = userRoleRepository.findByUserId(user.getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            UserResponse userResponse = UserMapping.convertToDto(user, hashIdsUtils);
            userResponse.setRoleName(dtbRole.getRoleName());
            userResponseList.add(userResponse);
        }
        // userPages.map(dtbUser ->UserMapping.convertToDto(dtbUser,
        // hashIdsUtils));
        Page<UserResponse> modifiedUserPages = new PageImpl<>(userResponseList, pageable, userPages.getTotalElements());

        return ResponseEntity.ok(modifiedUserPages);
    }

    protected List<Predicate> setUserListPredicate(
            Root<User> root, UserListSearchCriteria criteria, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        if (org.springframework.util.StringUtils.hasText(criteria.getCreatedAt())) {
            Path<LocalDateTime> createdAtPath = root.get("createdAt");
            LocalDate createdAt = DateTimeHelper.convertDateFromString(criteria.getCreatedAt(), "yyyy-MM-dd");
            LocalDateTime dateFrom = createdAt.atStartOfDay();
            Predicate createdFromPredicate = builder.greaterThanOrEqualTo(createdAtPath, dateFrom);
            predicates.add(createdFromPredicate);

            LocalDateTime dateTo = createdAt.atTime(23, 59, 59);
            Predicate createdToPredicate = builder.lessThanOrEqualTo(createdAtPath, dateTo);
            predicates.add(createdToPredicate);
        }

        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }

        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"), dateTo);

            predicates.add(dateToPredicate);
        }

        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class), false);
        predicates.add(isDeletedPredicate);

        if (org.springframework.util.StringUtils.hasText(criteria.getRoleName())) {
            Join<User, Role> rolesJoin = root.join("roles", JoinType.LEFT);
            Path<String> roleNamePath = rolesJoin.get("roleName");
            Predicate roleNamePredicate = builder.equal(roleNamePath, criteria.getRoleName());
            predicates.add(roleNamePredicate);
        }

        if (org.springframework.util.StringUtils.hasText(criteria.getFullName())) {
            Path<String> userNamePath = root.get("fullName");
            Predicate userNamePredicate = builder.like(userNamePath, "%" + criteria.getFullName() + "%");
            predicates.add(userNamePredicate);
        }

        return predicates;
    }

    public ResponseEntity<UserResponse> deleteUserById(String id) {
        Integer userId = hashIdsUtils.decodeId(id);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.softDelete(user.get());
            return ResponseEntity.ok(UserMapping.convertToDto(user.get(), hashIdsUtils));
        }
        throw new NotFoundException("user not found");
    }

    public ResponseEntity<UserResponse> resetPassword(String id) {
        Integer userId = hashIdsUtils.decodeId(id);
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new NotFoundException("user not found");
        }
        User dtbUser = user.get();
        dtbUser.setPassword(bCryptPasswordEncoder.encode("123456"));
        User updatedUser = userRepository.save(dtbUser);
        return ResponseEntity.ok(UserMapping.convertToDto(updatedUser, hashIdsUtils));
    }

    public ResponseEntity<UserResponse> unLockUser(String id) {
        Integer userId = hashIdsUtils.decodeId(id);
        Optional<User> dtbUser = userRepository.findById(userId).filter(dt -> !dt.isDeleted());
        if (dtbUser.isPresent()) {
            User user = dtbUser.get();
            user.setIsLocked(false);
            User updatedUser = userRepository.save(user);

            UserRole userRole = userRoleRepository.findByUserId(user.getId());
            Role dtbRole = roleRepository.getById(userRole.getRoleId());
            UserResponse userResponse = UserMapping.convertToDto(updatedUser, hashIdsUtils);
            userResponse.setRoleName(dtbRole.getRoleName());
            return ResponseEntity.ok(userResponse);
        }
        throw new NotFoundException("Not found user");
    }

    public ResponseEntity<UserResponse> changePassword(ChangePassword changePassword) {

        Optional<User> userOptional = userRepository.findByUserNameAndIsDeletedFalse(changePassword.getUsername());
        if (userOptional.isPresent()) {
            User dtbUser = userOptional.get();
            // Validate the old password
            if (!bCryptPasswordEncoder.matches(changePassword.getOldPassword(), dtbUser.getPassword())) {
                throw new NotFoundException("Password is incorrect");
            }
            // Update the password

            dtbUser.setPassword(bCryptPasswordEncoder.encode(changePassword.getNewPassword()));
            userRepository.save(dtbUser);
            return ResponseEntity.ok(UserMapping.convertToDto(dtbUser, hashIdsUtils));
        } else {
            throw new NotFoundException("Not found user");
        }
    }

    public ResponseEntity<UserResponse> getDetail(String username) {

        Optional<User> userOptional = userRepository.findByUserNameAndIsDeletedFalse(username);
        if (!userOptional.isPresent()) {
            throw new NotFoundException("Not found user");
        }
        User dtbUser = userOptional.get();
        return ResponseEntity.ok(UserMapping.convertToDto(dtbUser, hashIdsUtils));

    }
}
