package com.ems.application.service.order;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.order.GetOrderByTable;
import com.ems.application.dto.order.MergeOrderRequest;
import com.ems.application.dto.order.NewOrderRequest;
import com.ems.application.dto.order.OrderListSearchCriteria;
import com.ems.application.dto.order.OrderResponse;
import com.ems.application.dto.order.ProductByOrderResponse;
import com.ems.application.dto.orderdetail.NewOrderDetailRequest;
import com.ems.application.dto.orderdetail.OrderDetailResponse;
import com.ems.application.entity.OrderDetail;
import com.ems.application.entity.Orders;
import com.ems.application.entity.Product;
import com.ems.application.entity.Tables;
import com.ems.application.entity.User;
import com.ems.application.enums.OrderDetailStatus;
import com.ems.application.enums.OrderStatus;
import com.ems.application.enums.TableStatus;
import com.ems.application.mapping.order.OrderMapping;
import com.ems.application.repository.OrderDetailRepository;
import com.ems.application.repository.OrderRepository;
import com.ems.application.repository.ProductRepository;
import com.ems.application.repository.TableRepository;
import com.ems.application.repository.UserRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class OrderService extends BaseService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final HashIdsUtils hashIdsUtils;

    public OrderService(OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            TableRepository tableRepository,
            UserRepository userRepository,
            HashIdsUtils hashIdsUtils) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<OrderResponse> createNewOrder(NewOrderRequest orderRequest) {
        Orders dtbOrderEntity = new Orders();
        Orders dtbOrder = OrderMapping.convertToEntity(orderRequest, dtbOrderEntity, hashIdsUtils);
        dtbOrder.setStatus(OrderStatus.IN_PROGRESS.getValue());
        orderRepository.save(dtbOrder);
        if (orderRequest.getOrderDetails().isEmpty()) {
            return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
        }
        Tables dtbTable = tableRepository
                .findByIdAndStatus(hashIdsUtils.decodeId(orderRequest.getTableId()), TableStatus.EMPTY.getValue());
        if (dtbTable == null) {
            return ResponseEntity.badRequest().build();
        }
        dtbTable.setStatus(TableStatus.PENDING.getValue());
        for (NewOrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(dtbOrder);
            orderDetail.setProductId(hashIdsUtils.decodeId(orderDetailRequest.getProductId()));
            orderDetail.setQuantity(orderDetailRequest.getQuantity());
            orderDetail.setNote(orderDetailRequest.getNote());
            orderDetail.setStatus(OrderDetailStatus.PENDING.getValue());
            orderDetailRepository.save(orderDetail);
        }
        return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
    }

    public ResponseEntity<Page<OrderResponse>> getAllOrder(OrderListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));

        Page<Orders> orderPages = orderRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setOrderListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (Orders order : orderPages) {
            Integer tableId = order.getTableId();
            Tables table = tableRepository.findById(tableId).orElse(null);
            String tableName = table != null ? table.getName() : "";
            String position = table != null ? table.getPosition() : "";
            OrderResponse orderResponse = OrderMapping.convertToDto(order, hashIdsUtils);
            Integer totalQuantity = 0;
            Double totalPrice = 0.0;
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
                Integer quantity = orderDetail.getPay();
                totalQuantity += quantity;
                totalPrice += product.getPrice() * quantity;
            }
            orderResponse.setTotalQuantity(totalQuantity);
            orderResponse.setTotalPrice(totalPrice);
            orderResponse.setTableName(tableName);
            orderResponse.setPosition(position);
            Integer cashierId = order.getUpdatedBy();
            Optional<User> cashier = userRepository.findById(cashierId);
            if (cashier.isPresent() && order.getStatus() > OrderStatus.DONE.getValue()) {
                orderResponse.setCashierName(cashier.get().getFullName());
                if (criteria.getCashierName() != null) {
                    if (!cashier.get().getFullName().toLowerCase().contains(criteria.getCashierName().toLowerCase())) {
                        continue;
                    }
                }
            }
            if (order.getStatus() == OrderStatus.CLOSE_BANKING.getValue()) {
                orderResponse.setPaymentMethod("Chuyển khoản");
            }
            if (order.getStatus() == OrderStatus.CLOSE_CASH.getValue()) {
                orderResponse.setPaymentMethod("Tiền mặt");
            }

            orderResponseList.add(orderResponse);
        }
        Page<OrderResponse> modifiedOrderPages = new PageImpl<>(orderResponseList,
                pageable,
                orderPages.getTotalElements());

        return ResponseEntity.ok(modifiedOrderPages);
    }

    public ResponseEntity<Page<OrderResponse>> getAllCurrentOrder(OrderListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));

        Page<Orders> orderPages = orderRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setCurrentOrderListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (Orders order : orderPages) {
            Integer tableId = order.getTableId();
            System.out.println("tableId: " + tableId);
            Tables table = tableRepository.findById(tableId).orElse(null);
            String tableName = table != null ? table.getName() : "";
            String position = table != null ? table.getPosition() : "";
            OrderResponse orderResponse = OrderMapping.convertToDto(order, hashIdsUtils);
            Integer totalQuantity = 0;
            Double totalPrice = 0.0;
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
            for (OrderDetail orderDetail : orderDetails) {
                Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
                Integer quantity = orderDetail.getPending() + orderDetail.getInProgress() + orderDetail.getServing()
                        + orderDetail.getDone() + orderDetail.getPay();
                totalQuantity += quantity;
                totalPrice += product.getPrice() * quantity;
            }
            orderResponse.setTotalQuantity(totalQuantity);
            orderResponse.setTotalPrice(totalPrice);
            orderResponse.setTableName(tableName);
            orderResponse.setPosition(position);
            orderResponseList.add(orderResponse);
        }
        Page<OrderResponse> modifiedOrderPages = new PageImpl<>(orderResponseList,
                pageable,
                orderPages.getTotalElements());

        return ResponseEntity.ok(modifiedOrderPages);
    }

    public ResponseEntity<Page<ProductByOrderResponse>> getProductByOrder(OrderListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        criteria.isDescending() ? Sort.Direction.DESC : Sort.Direction.ASC,
                        criteria.getSortBy()));

        List<ProductByOrderResponse> productByOrderResponses = new ArrayList<>();
        Integer status = criteria.getStatus();
        List<Object[]> productByOrder = orderRepository.getProductByOrder(status);
        for (Object[] product : productByOrder) {
            ProductByOrderResponse productByOrderResponse = new ProductByOrderResponse();
            Integer odID = Integer.parseInt(product[0].toString());
            productByOrderResponse.setId(hashIdsUtils.encodeId(odID));
            productByOrderResponse.setName(product[1].toString());
            if (product[2] != null) {
                productByOrderResponse.setNote(product[2].toString());
            }
            if (product[3] != null) {
                productByOrderResponse.setQuantity(Integer.parseInt(product[3].toString()));
            }
            productByOrderResponses.add(productByOrderResponse);
        }
        return ResponseEntity.ok(new PageImpl<>(productByOrderResponses, pageable,
                productByOrderResponses.size()));
    }

    protected List<Predicate> setOrderListPredicate(
            Root<Orders> root, OrderListSearchCriteria criteria, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }

        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"),
                    dateTo);

            predicates.add(dateToPredicate);
        }
        if (criteria.getStatus() != null) {
            if (criteria.getStatus() > OrderStatus.CLOSE_CASH.getValue()) {
                Path<Integer> statusPath = root.get("status");
                Predicate status5Predicate = builder.equal(statusPath.as(Integer.class),
                        OrderStatus.CLOSE_BANKING.getValue());
                Predicate status6Predicate = builder.equal(statusPath.as(Integer.class),
                        OrderStatus.CLOSE_CASH.getValue());
                Predicate statusPredicate = builder.or(status5Predicate, status6Predicate);
                predicates.add(statusPredicate);
            } else {
                Path<Integer> statusPath = root.get("status");
                Predicate statusPredicate = builder.equal(statusPath.as(Integer.class),
                        criteria.getStatus());
                predicates.add(statusPredicate);
            }
        }

        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        predicates.add(isDeletedPredicate);

        return predicates;
    }

    protected List<Predicate> setCurrentOrderListPredicate(
            Root<Orders> root, OrderListSearchCriteria criteria, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        if (org.springframework.util.StringUtils.hasText(criteria.getDateFrom())) {
            LocalDateTime dateFrom = DateTimeHelper.convertToLocalDateTime(criteria.getDateFrom());
            Predicate dateFromPredicate = builder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);

            predicates.add(dateFromPredicate);
        }

        if (org.springframework.util.StringUtils.hasText(criteria.getDateTo())) {
            LocalDateTime dateTo = DateTimeHelper.convertToLocalDateTime(criteria.getDateTo());
            Predicate dateToPredicate = builder.lessThanOrEqualTo(root.get("createdAt"),
                    dateTo);

            predicates.add(dateToPredicate);
        }

        Path<Integer> statusPath = root.get("status");
        Predicate statusPredicate = builder.lessThan(statusPath.as(Integer.class),
                OrderStatus.DONE.getValue());
        predicates.add(statusPredicate);

        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        predicates.add(isDeletedPredicate);

        return predicates;
    }

    public ResponseEntity<OrderResponse> getOrderById(String id, Integer status) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        OrderResponse orderResponse = new OrderResponse();
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<OrderDetail> tmpOrderDetails = new ArrayList<>();

        tmpOrderDetails = orderDetailRepository.findByOrder(order);
        if (status != null && status != 0) {
            for (OrderDetail orderDetail : tmpOrderDetails) {
                if (status == OrderDetailStatus.PENDING.getValue()) {
                    if (orderDetail.getPending() > 0) {
                        orderDetails.add(orderDetail);
                    } else {
                        continue;
                    }
                }
                if (status == OrderDetailStatus.PROCESSING.getValue()) {
                    if (orderDetail.getInProgress() > 0) {
                        orderDetails.add(orderDetail);
                    } else {
                        continue;
                    }
                }
                if (status == OrderDetailStatus.SERVING.getValue()) {
                    if (orderDetail.getServing() > 0) {
                        orderDetails.add(orderDetail);
                    } else {
                        continue;
                    }
                }
                if (status == OrderDetailStatus.SERVED.getValue()) {
                    if (orderDetail.getDone() > 0) {
                        orderDetails.add(orderDetail);
                    } else {
                        continue;
                    }
                }
            }
        } else {
            orderDetails = tmpOrderDetails;
        }
        orderResponse = OrderMapping.convertToDto(order, hashIdsUtils);
        Tables table = tableRepository.findById(order.getTableId()).get();
        orderResponse.setTableName(table.getName());
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
        Integer totalQuantity = 0;
        Double totalPrice = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setId(hashIdsUtils.encodeId(orderDetail.getId()));
            orderDetailResponse.setProductId(hashIdsUtils.encodeId(orderDetail.getProductId()));
            orderDetailResponse.setQuantity(orderDetail.getQuantity());
            orderDetailResponse.setNote(orderDetail.getNote());
            orderDetailResponse.setProductName(product.getName());
            orderDetailResponse.setImage(product.getImage());
            orderDetailResponse.setPrice(product.getPrice());
            orderDetailResponse.setStatus(orderDetail.getStatus());
            orderDetailResponse.setServing(orderDetail.getServing());
            orderDetailResponse.setInProgress(orderDetail.getInProgress());
            orderDetailResponse.setDone(orderDetail.getDone());
            orderDetailResponse.setPending(orderDetail.getPending());
            orderDetailResponse.setPay(orderDetail.getPay());
            totalPrice += product.getPrice() * orderDetail.getQuantity();
            totalQuantity += orderDetail.getQuantity();
            orderDetailResponses.add(orderDetailResponse);
        }
        orderResponse.setCashierName(
                order.getUpdatedBy() != null ? userRepository.findById(order.getUpdatedBy()).get().getFullName() : "");
        orderResponse.setTotalQuantity(totalQuantity);
        orderResponse.setTotalPrice(totalPrice);
        orderResponse.setOrderDetails(orderDetailResponses);
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<OrderResponse> getOrderByTableId(GetOrderByTable orderRequest) {
        Orders order = orderRepository.findByTableIdAndStatusLessThan(hashIdsUtils.decodeId(orderRequest.getTableId()),
                OrderStatus.DONE.getValue());
        if (order != null) {
            return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
        }
        Orders dtbOrderEntity = new Orders();
        dtbOrderEntity.setTableId(hashIdsUtils.decodeId(orderRequest.getTableId()));
        dtbOrderEntity.setCustomerName(orderRequest.getCustomerName());
        Orders savedOrder = orderRepository.save(dtbOrderEntity);
        Tables table = tableRepository.findById(dtbOrderEntity.getTableId()).get();
        OrderResponse orderResponse = OrderMapping.convertToDto(savedOrder, hashIdsUtils);
        orderResponse.setTableName(table.getName());
        orderResponse.setPosition(table.getPosition());
        table.setStatus(TableStatus.PENDING.getValue());
        tableRepository.save(table);
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<OrderResponse> updateOrder(String id, NewOrderRequest orderRequest) {
        Orders dtbOrder = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (dtbOrder == null) {
            return ResponseEntity.notFound().build();
        }
        if (orderRequest.getOrderDetails().isEmpty()) {
            return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderAndStatus(dtbOrder, 1);
        for (OrderDetail orderDetail : orderDetails) {
            Boolean isInOrder = false;
            for (NewOrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
                Integer odId = hashIdsUtils.decodeId(orderDetailRequest.getId());
                if (orderDetail.getId().equals(odId)) {
                    isInOrder = true;
                    Product product = productRepository
                            .findById(hashIdsUtils.decodeId(orderDetailRequest.getProductId()))
                            .orElse(null);
                    if (product == null) {
                        return ResponseEntity.badRequest().build();
                    }
                    orderDetail.setPrice(product.getPrice());
                    orderDetail.setQuantity(orderDetailRequest.getQuantity());
                    orderDetail.setPending(orderDetailRequest.getPending());
                    orderDetail.setNote(orderDetailRequest.getNote());
                    orderDetailRepository.save(orderDetail);
                    break;
                }
            }
            if (!isInOrder && orderDetail.getQuantity() == 0) {
                orderDetailRepository.delete(orderDetail);
            }
        }
        return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> addFromCart(String id, NewOrderRequest orderRequest) {
        Orders dtbOrder = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (dtbOrder == null) {
            return ResponseEntity.notFound().build();
        }
        if (orderRequest.getOrderDetails().isEmpty()) {
            return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
        }
        for (NewOrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
            OrderDetail orderDetail = orderDetailRepository.findByOrderAndProductId(dtbOrder,
                    hashIdsUtils.decodeId(orderDetailRequest.getProductId()));
            Product product = productRepository.findById(hashIdsUtils.decodeId(orderDetailRequest.getProductId()))
                    .orElse(null);
            if (orderDetail != null) {
                orderDetail.setPending(orderDetail.getPending() + orderDetailRequest.getQuantity());
                orderDetail.setStatus(OrderDetailStatus.PENDING.getValue());
                orderDetail.setNote(orderDetailRequest.getNote());
                orderDetail.setPrice(product.getPrice());
                orderDetailRepository.save(orderDetail);
                continue;
            }
            orderDetail = new OrderDetail();
            orderDetail.setOrder(dtbOrder);
            orderDetail.setQuantity(0);
            orderDetail.setProductId(hashIdsUtils.decodeId(orderDetailRequest.getProductId()));
            orderDetail.setPending(orderDetailRequest.getQuantity());
            orderDetail.setNote(orderDetailRequest.getNote());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setStatus(OrderDetailStatus.PENDING.getValue());
            orderDetailRepository.save(orderDetail);
        }
        dtbOrder.setStatus(OrderStatus.IN_PROGRESS.getValue());
        Tables table = tableRepository.findById(dtbOrder.getTableId()).get();
        table.setStatus(TableStatus.PENDING.getValue());
        return ResponseEntity.ok(OrderMapping.convertToDto(dtbOrder, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> deleteOrder(String id) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setDeleted(true);
        orderRepository.save(order);
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> processOrder(String id, NewOrderRequest orderRequest) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        List<NewOrderDetailRequest> orderDetailList = orderRequest.getOrderDetails();
        for (NewOrderDetailRequest orderDetailRequest : orderDetailList) {
            OrderDetail orderDetail = orderDetailRepository.findByOrderAndProductId(order,
                    hashIdsUtils.decodeId(orderDetailRequest.getProductId()));
            if (orderDetail != null) {
                orderDetail.setQuantity(orderDetailRequest.getPending() + orderDetail.getQuantity());
                orderDetail.setNote(orderDetailRequest.getNote());
                orderDetail.setPrice(orderDetailRequest.getPrice());
                orderDetail.setInProgress(orderDetailRequest.getPending() + orderDetail.getInProgress());
                orderDetail.setPending(0);
                orderDetailRepository.save(orderDetail);
            }
        }
        Tables table = tableRepository.findById(order.getTableId()).get();
        table.setStatus(TableStatus.PENDING.getValue());
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> completeOrder(String id, NewOrderRequest orderRequest) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.setStatus(OrderStatus.DONE.getValue());
        orderRepository.save(order);
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        for (OrderDetail orderDetail : orderDetails) {
            Boolean isInOrder = false;
            for (NewOrderDetailRequest orderDetailRequest : orderRequest.getOrderDetails()) {
                if (orderDetail.getId().equals(hashIdsUtils.decodeId(orderDetailRequest.getId()))) {
                    isInOrder = true;
                    orderDetail.setPay(orderDetailRequest.getQuantity());
                    orderDetail.setInProgress(0);
                    orderDetail.setServing(0);
                    orderDetail.setPending(0);
                    orderDetail.setDone(0);
                    orderDetail.setQuantity(orderDetailRequest.getQuantity());
                    orderDetail.setNote(orderDetailRequest.getNote());
                    orderDetail.setStatus(orderDetailRequest.getStatus());
                    orderDetailRepository.save(orderDetail);
                }
            }
            if (!isInOrder) {
                orderDetailRepository.delete(orderDetail);
                ;
            }
        }
        Tables table = tableRepository.findById(order.getTableId()).get();
        table.setStatus(TableStatus.DONE.getValue());
        tableRepository.save(table);
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> closeOrder(String id, NewOrderRequest orderRequest) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        Integer totalQuantity = 0;
        Double totalPrice = 0.0;
        for (OrderDetail orderDetail : orderDetailRepository.findByOrder(order)) {
            Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
            Integer quantity = orderDetail.getPay();
            totalQuantity += quantity;
            totalPrice += product.getPrice() * quantity;
        }
        order.setStatus(orderRequest.getStatus() == 5 ? OrderStatus.CLOSE_BANKING.getValue()
                : OrderStatus.CLOSE_CASH.getValue());
        order.setCustomerName(orderRequest.getCustomerName());
        order.setTotalQuantity(totalQuantity);
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
        Tables table = tableRepository.findById(order.getTableId()).get();
        table.setStatus(TableStatus.EMPTY.getValue());
        tableRepository.save(table);
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> callWaiter(String id) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        Tables table = tableRepository.findById(order.getTableId()).get();
        table.setStatus(TableStatus.WAIT.getValue());
        tableRepository.save(table);
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> mergeOrder(String id, MergeOrderRequest orderRequest) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        Orders orderToMerge = orderRepository.findById(hashIdsUtils.decodeId(orderRequest.getOrderId())).orElse(null);
        if (order == null || orderToMerge == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        List<OrderDetail> orderDetailsToMerge = orderDetailRepository.findByOrder(orderToMerge);
        for (OrderDetail orderDetailToMerge : orderDetailsToMerge) {
            Boolean isInOrder = false;
            for (OrderDetail orderDetail : orderDetails) {
                if (orderDetailToMerge.getProductId().equals(orderDetail.getProductId())) {
                    isInOrder = true;
                    orderDetail.setQuantity(orderDetail.getQuantity() + orderDetailToMerge.getQuantity());
                    orderDetail.setPending(orderDetail.getPending() + orderDetailToMerge.getPending());
                    orderDetail.setInProgress(orderDetail.getInProgress() + orderDetailToMerge.getInProgress());
                    orderDetail.setServing(orderDetail.getServing() + orderDetailToMerge.getServing());
                    orderDetail.setDone(orderDetail.getDone() + orderDetailToMerge.getDone());
                    orderDetail.setPay(orderDetail.getPay() + orderDetailToMerge.getPay());
                    orderDetailRepository.delete(orderDetailToMerge);
                    break;
                }
                if (!isInOrder) {
                    orderDetailToMerge.setOrder(order);
                    orderDetailRepository.save(orderDetailToMerge);
                }
            }
        }
        orderRepository.delete(orderToMerge);
        return ResponseEntity.ok(OrderMapping.convertToDto(order, hashIdsUtils));
    }

    public ResponseEntity<OrderResponse> splitOrder(String id, NewOrderRequest orderRequest) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        Orders newOrder = new Orders();
        newOrder.setTableId(order.getTableId());
        newOrder.setCustomerName(order.getCustomerName());
        newOrder.setStatus(OrderStatus.IN_PROGRESS.getValue());
        orderRepository.save(newOrder);
        return ResponseEntity.ok(OrderMapping.convertToDto(newOrder, hashIdsUtils));
    }

    public ResponseEntity<Resource> exportToExcel(String id, String customerName, Integer status) {
        Orders order = orderRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Order Details");

            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Order ID");
            row.createCell(1).setCellValue(order.getId());

            row = sheet.createRow(1);
            row.createCell(0).setCellValue("Customer Name");
            row.createCell(1).setCellValue(customerName);

            row = sheet.createRow(2);
            row.createCell(0).setCellValue("Payment Method");
            if (status == OrderStatus.CLOSE_BANKING.getValue()) {
                row.createCell(1).setCellValue("Chuyển khoản");
            } else {
                row.createCell(1).setCellValue("Tiền mặt");
            }

            row = sheet.createRow(3);
            row.createCell(0).setCellValue("Product Name");
            row.createCell(1).setCellValue("Quantity");
            row.createCell(2).setCellValue("Price");
            row.createCell(3).setCellValue("Total");
            int rowNum = 4;
            for (OrderDetail orderDetail : orderDetails) {
                row = sheet.createRow(rowNum++);
                Product product = productRepository.findById(orderDetail.getProductId()).orElse(null);
                row.createCell(0).setCellValue(product != null ? product.getName() : "");
                row.createCell(1).setCellValue(orderDetail.getQuantity());
                row.createCell(2).setCellValue(orderDetail.getPrice());
                row.createCell(3).setCellValue(orderDetail.getQuantity() * orderDetail.getPrice());
            }
            row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Total");
            row.createCell(1).setCellValue("");
            row.createCell(2).setCellValue("");
            row.createCell(3).setCellValue(orderDetails.stream()
                    .mapToDouble(orderDetail -> orderDetail.getQuantity() * orderDetail.getPrice()).sum());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            byte[] excelContent = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "order_" + id + ".xlsx");

            ByteArrayResource resource = new ByteArrayResource(excelContent);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}