package com.ems.application.service.role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.role.NewRoleRequest;
import com.ems.application.dto.role.RoleListSearchCriteria;
import com.ems.application.dto.role.RoleResponse;
import com.ems.application.entity.Role;
import com.ems.application.exception.NotFoundException;
import com.ems.application.mapping.role.RoleMapping;
import com.ems.application.repository.RoleRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class RoleService extends BaseService {

    private final RoleRepository roleRepository;
    private final HashIdsUtils hashIdsUtils;

    public RoleService(RoleRepository roleRepository, HashIdsUtils hashIdsUtils) {
        this.roleRepository = roleRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<RoleResponse> createNewRole(NewRoleRequest roleRequest) {
        Role dtbRoleEntity = new Role();
        Role dtbRole = RoleMapping.convertToEntity(roleRequest, dtbRoleEntity);
        Role roleResponse = roleRepository.save(dtbRole);
        return ResponseEntity.ok(RoleMapping.convertToDto(roleResponse, hashIdsUtils));
    }

    public ResponseEntity<RoleResponse> updateRole(int id, NewRoleRequest roleRequest) {
        Optional<Role> role = roleRepository.findById(id).filter(dt -> !dt.isDeleted());
        if (role.isPresent()) {
            Role dtbRoleEntity = role.get();
            Role dtbRole = RoleMapping.convertToEntity(roleRequest, dtbRoleEntity);
            Role roleResponse = roleRepository.save(dtbRole);
            return ResponseEntity.ok(RoleMapping.convertToDto(roleResponse, hashIdsUtils));
        }
        throw new NotFoundException("Role not found");
    }

    // public ResponseEntity<List<RoleResponse>> getAllRoles() {
    // List<DtbRttRole> roles = roleRepository.findAll()
    // .stream()
    // .filter(dt -> !dt.isDeleted())
    // .collect(Collectors.toList());

    // List<RoleResponse> roleResponses = roles.stream()
    // .map(dtbrole -> RoleMapping.convertToDto(dtbrole, hashIdsUtils))
    // .collect(Collectors.toList());

    // return ResponseEntity.ok(roleResponses);
    // }

    public ResponseEntity<Page<RoleResponse>> getAllRoles(RoleListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));

        Page<Role> rolePages = roleRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setRoleListPredicate(root, criteria, builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        return ResponseEntity.ok(rolePages.map(dtbRole -> RoleMapping.convertToDto(dtbRole, hashIdsUtils)));
    }

    protected List<Predicate> setRoleListPredicate(
            Root<Role> root, RoleListSearchCriteria criteria, CriteriaBuilder builder) {

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

        return predicates;
    }

    public ResponseEntity<RoleResponse> getRoleById(int id) {
        Optional<Role> role = roleRepository.findById(id).filter(dt -> !dt.isDeleted());
        if (role.isPresent()) {
            return ResponseEntity.ok(RoleMapping.convertToDto(role.get(), hashIdsUtils));
        }
        throw new NotFoundException("Role not found");
    }

    public ResponseEntity<RoleResponse> deleteRoleById(int id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            roleRepository.softDelete(role.get());
            return ResponseEntity.ok(RoleMapping.convertToDto(role.get(), hashIdsUtils));
        }
        throw new NotFoundException("Role type not found");
    }

}
