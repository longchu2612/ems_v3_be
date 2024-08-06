package com.ems.application.service.table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.order.OrderResponse;
import com.ems.application.dto.orderdetail.OrderDetailResponse;
import com.ems.application.dto.table.NewTableRequest;
import com.ems.application.dto.table.TableListSearchCriteria;
import com.ems.application.dto.table.TableResponse;
import com.ems.application.entity.OrderDetail;
import com.ems.application.entity.Orders;
import com.ems.application.entity.Product;
import com.ems.application.entity.Tables;
import com.ems.application.enums.OrderStatus;
import com.ems.application.mapping.order.OrderMapping;
import com.ems.application.mapping.table.TableMapping;
import com.ems.application.repository.OrderDetailRepository;
import com.ems.application.repository.OrderRepository;
import com.ems.application.repository.ProductRepository;
import com.ems.application.repository.TableRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class TableService extends BaseService {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final HashIdsUtils hashIdsUtils;

    public TableService(TableRepository tableRepository,
            OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            HashIdsUtils hashIdsUtils) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<TableResponse> createNewTable(NewTableRequest tableRequest) {
        // Initialize a table object.
        Tables dtbTableEntity = tableRepository.findByName(tableRequest.getName());
        if (dtbTableEntity != null) {
            return ResponseEntity.badRequest().build();
        }
        dtbTableEntity = new Tables();
        // Convert table object to enitty.
        Tables dtbTable = TableMapping.convertToEntity(tableRequest, dtbTableEntity);
        // Save the table object to the database via tableRepository
        tableRepository.save(dtbTable);
        // return status and a dto object
        return ResponseEntity.ok(TableMapping.convertToDto(dtbTable, hashIdsUtils));
    }

    public ResponseEntity<Page<TableResponse>> getAllTable(TableListSearchCriteria criteria) {

        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));
        Page<Tables> tablePages = tableRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setTableListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        List<TableResponse> tableResponseList = new ArrayList<>();
        tablePages.forEach(table -> tableResponseList.add(TableMapping.convertToDto(table, hashIdsUtils)));

        Page<TableResponse> modifiedTablePages = new PageImpl<>(tableResponseList, pageable,
                tablePages.getTotalElements());

        return ResponseEntity.ok(modifiedTablePages);
    }

    protected List<Predicate> setTableListPredicate(
            Root<Tables> root, TableListSearchCriteria criteria, CriteriaBuilder builder) {

        // Initializes a list to store predicates.
        List<Predicate> predicates = new ArrayList<>();
        // Checks if the search criteria includes a starting date.
        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }
        // Checks if the search criteria includes an ending date.
        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"),
                    dateTo);

            predicates.add(dateToPredicate);
        }
        // Retrieves the attribute "isDeleted" from the root entity (Tables) to filter
        // entities that are not deleted.
        if (criteria.getStatus() > 0) {
            Path<Integer> statusPath = root.get("status");
            Predicate statusPredicate = builder.equal(statusPath.as(Integer.class),
                    criteria.getStatus());
            predicates.add(statusPredicate);

        }
        Path<Boolean> isDeletedPath = root.get("isDeleted");
        // Creates a predicate to filter entities where isDeleted is false.
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        // Adds the predicate to the list of predicates.
        predicates.add(isDeletedPredicate);
        // Checks if the search criteria includes a table name.
        if (org.springframework.util.StringUtils.hasText(criteria.getName())) {
            Path<String> tableNamePath = root.get("tableName");
            Predicate tableNamePredicate = builder.like(tableNamePath, "%" +
                    criteria.getName() + "%");
            predicates.add(tableNamePredicate);
        }
        // Returns the list of predicates.
        return predicates;
    }

    public ResponseEntity<TableResponse> getTableById(String id) {
        Tables table = tableRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        Orders order = orderRepository.findByTableIdAndStatus(hashIdsUtils.decodeId(id),
                OrderStatus.IN_PROGRESS.getValue());
        if (table == null) {
            return ResponseEntity.notFound().build();
        }

        OrderResponse orderResponse = OrderMapping.convertToDto(order, hashIdsUtils);
        TableResponse tableResponse = TableMapping.convertToDto(table, hashIdsUtils);
        if (order == null) {
            tableResponse.setOrder(null);
            return ResponseEntity.ok(tableResponse);
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        Integer totalQuantity = 0;
        Double totalPrice = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setId(hashIdsUtils.encodeId(orderDetail.getId()));
            // orderDetailResponse.setProductId(orderDetail.getProductId());
            orderDetailResponse.setProductId(hashIdsUtils.encodeId(orderDetail.getProductId()));
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setNote(orderDetail.getNote());
            orderDetailResponse.setProductName(product.getName());
            orderDetailResponse.setImage(product.getImage());
            // // Set price to the response from the product and calculate total quantity
            // and total price.
            orderDetailResponse.setPrice(product.getPrice());
            totalQuantity += orderDetail.getQuantity();
            totalPrice += product.getPrice() * orderDetail.getQuantity();
            orderDetailResponses.add(orderDetailResponse);
        }
        orderResponse.setTotalQuantity(totalQuantity);
        orderResponse.setTotalPrice(totalPrice);
        orderResponse.setOrderDetails(orderDetailResponses);
        tableResponse.setOrder(orderResponse);
        return ResponseEntity.ok(tableResponse);
    }

    public ResponseEntity<TableResponse> updateTable(String id, NewTableRequest tableRequest) {
        Tables table = tableRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (table == null) {
            return ResponseEntity.notFound().build();
        }
        Tables updatedTable = TableMapping.convertToEntity(tableRequest, table);
        tableRepository.save(updatedTable);
        return ResponseEntity.ok(TableMapping.convertToDto(updatedTable, hashIdsUtils));
    }

    public ResponseEntity<TableResponse> deleteTable(String id) {
        // Decodes the provided ID using hashIdsUtils to obtain the original ID.
        // Then attempts to find a Table entity with the decoded ID from the repository.
        // If a table with the specified ID is found, it is assigned to the 'table'
        // variable.
        // If no table is found, 'table' will be assigned null.
        Tables table = tableRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // Checks if 'table' is null, indicating that no table was found with the
        // provided ID.
        if (table == null) {
            // If no table is found, returns a ResponseEntity with HTTP status code 404 (Not
            // Found).
            return ResponseEntity.notFound().build();
        }
        // Marks the table as deleted by setting its 'deleted' attribute to true.
        table.setDeleted(true);
        // Saves the updated table entity with the 'deleted' attribute set to true to
        // the repository.
        tableRepository.save(table);
        // Converts the deleted Table entity to a TableResponse DTO using
        // TableMapping.convertToDto method.
        // The hashIdsUtils parameter might be used for additional functionality like
        // encryption or decryption of identifiers.
        // Then returns a ResponseEntity with the deleted TableResponse DTO and HTTP
        // status code 200 (OK).
        return ResponseEntity.ok(TableMapping.convertToDto(table, hashIdsUtils));
    }
}