package com.ems.application.service.orderdetail;

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

import com.ems.application.dto.base.BaseResponse;
import com.ems.application.dto.order.OrderListSearchCriteria;
import com.ems.application.dto.orderdetail.OrderDetailResponse;
import com.ems.application.dto.orderdetail.ProcessOrderDetail;
import com.ems.application.entity.OrderDetail;
import com.ems.application.entity.Orders;
import com.ems.application.entity.Product;
import com.ems.application.entity.Tables;
import com.ems.application.enums.OrderDetailStatus;
import com.ems.application.enums.OrderStatus;
import com.ems.application.enums.TableStatus;
import com.ems.application.repository.OrderDetailRepository;
import com.ems.application.repository.OrderRepository;
import com.ems.application.repository.ProductRepository;
import com.ems.application.repository.TableRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

@Service

public class OrderDetailService extends BaseService {

    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final HashIdsUtils hashIdsUtils;

    public OrderDetailService(
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            TableRepository tableRepository,
            HashIdsUtils hashIdsUtils) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<BaseResponse> processOrderDetail(String id, ProcessOrderDetail processOrderDetail) {
        Integer odID = hashIdsUtils.decodeId(id);
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        orderDetail.setInProgress(orderDetail.getInProgress() + processOrderDetail.getQuantity());
        orderDetail.setServing(orderDetail.getServing() - processOrderDetail.getQuantity());
        orderDetail.setStatus(OrderDetailStatus.PROCESSING.getValue());
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BaseResponse> pendOrderDetail(String id) {
        Integer odID = hashIdsUtils.decodeId(id);
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        orderDetail.setStatus(OrderDetailStatus.PENDING.getValue());
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BaseResponse> serveOrderDetail(String id, ProcessOrderDetail processOrderDetail) {
        Integer odID = hashIdsUtils.decodeId(id);
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        orderDetail.setServing(orderDetail.getServing() + processOrderDetail.getQuantity());
        orderDetail.setInProgress(orderDetail.getInProgress() - processOrderDetail.getQuantity());
        if (orderDetail.getDone() == orderDetail.getQuantity()) {
            orderDetail.setStatus(OrderDetailStatus.SERVING.getValue());
        }
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BaseResponse> unServeOrderDetail(String id, ProcessOrderDetail processOrderDetail) {
        Integer odID = hashIdsUtils.decodeId(id);
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        orderDetail.setServing(orderDetail.getServing() + processOrderDetail.getQuantity());
        orderDetail.setDone(orderDetail.getDone() - processOrderDetail.getQuantity());
        if (orderDetail.getDone() == orderDetail.getQuantity()) {
            orderDetail.setStatus(OrderDetailStatus.SERVING.getValue());
        }
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<BaseResponse> servedOrderDetail(String id, ProcessOrderDetail processOrderDetail) {
        Integer odID = hashIdsUtils.decodeId(id);
        OrderDetail orderDetail = orderDetailRepository.findById(odID)
                .orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.notFound().build();
        }
        orderDetail.setDone(orderDetail.getDone() + processOrderDetail.getQuantity());
        orderDetail.setServing(orderDetail.getServing() - processOrderDetail.getQuantity());
        if (orderDetail.getDone() == orderDetail.getQuantity()) {
            orderDetail.setStatus(OrderDetailStatus.SERVED.getValue());
        }
        orderDetailRepository.save(orderDetail);
        Orders orders = orderRepository.getById(orderDetail.getOrder().getId());
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(orders);
        boolean isAllServed = true;
        for (OrderDetail od : orderDetails) {
            if (od.getServing() > 0) {
                isAllServed = false;
                break;
            }
        }
        if (isAllServed) {
            orders.setStatus(OrderStatus.SERVE_DONE.getValue());
            orderRepository.save(orders);
            Tables table = tableRepository.getById(orders.getTableId());
            table.setStatus(TableStatus.SERVED.getValue());
            tableRepository.save(table);
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Page<OrderDetailResponse>> getAllOrderDetail(OrderListSearchCriteria criteria) {
        Pageable pageable = createPageRequest(
                criteria.getPageIndex() - 1,
                criteria.getPageSize(),
                Sort.by(
                        Sort.Direction.ASC,
                        criteria.getSortBy()));

        Page<OrderDetail> orderDetailPages = orderDetailRepository.findAll(
                (root, query, builder) -> {
                    query.distinct(true);
                    List<Predicate> predicates = setOrderDetailListPredicate(root, criteria,
                            builder);
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                },
                pageable);
        List<OrderDetailResponse> orderDetailResponseList = new ArrayList<>();
        for (OrderDetail order : orderDetailPages) {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            orderDetailResponse.setId(hashIdsUtils.encodeId(order.getId()));
            orderDetailResponse.setNote(order.getNote());
            orderDetailResponse.setPrice(order.getPrice());
            orderDetailResponse.setProductId(hashIdsUtils.encodeId(order.getProductId()));
            orderDetailResponse.setQuantity(order.getQuantity());
            orderDetailResponse.setServing(order.getServing());
            orderDetailResponse.setPending(order.getPending());
            orderDetailResponse.setInProgress(order.getInProgress());
            orderDetailResponse.setDone(order.getDone());
            Orders orders = orderRepository.getById(order.getOrder().getId());
            Tables table = tableRepository.getById(orders.getTableId());
            orderDetailResponse.setTableName(table.getName());
            Product product = productRepository.findById(order.getProductId()).orElse(null);
            if (product != null) {
                orderDetailResponse.setImage(product.getImage());
                orderDetailResponse.setProductName(product.getName());
            }
            orderDetailResponseList.add(orderDetailResponse);

        }
        Page<OrderDetailResponse> modifiedOrderDetailPages = new PageImpl<>(orderDetailResponseList,
                pageable,
                orderDetailPages.getTotalElements());

        return ResponseEntity.ok(modifiedOrderDetailPages);
    }

    protected List<Predicate> setOrderDetailListPredicate(
            Root<OrderDetail> root, OrderListSearchCriteria criteria, CriteriaBuilder builder) {

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
        if (criteria.getStatus() > 0) {
            // if status = 1, get all order detail with inProgress = 0
            // if status = 2, get all order detail with inProgress > 0
            // if status = 3, get all order detail with serving > 0
            // if status = 4, get all order detail with done > 0

            if (criteria.getStatus() == 1) {
                Path<Integer> inProgressPath = root.get("pending");
                Predicate inProgressPredicate = builder.greaterThan(inProgressPath.as(Integer.class),
                        0);
                predicates.add(inProgressPredicate);
            } else if (criteria.getStatus() == 2) {
                Path<Integer> inProgressPath = root.get("inProgress");
                Predicate inProgressPredicate = builder.greaterThan(inProgressPath.as(Integer.class),
                        0);
                predicates.add(inProgressPredicate);
            } else if (criteria.getStatus() == 3) {
                Path<Integer> servingPath = root.get("serving");
                Predicate servingPredicate = builder.greaterThan(servingPath.as(Integer.class),
                        0);
                predicates.add(servingPredicate);
            } else if (criteria.getStatus() == 4) {
                Path<Integer> donePath = root.get("done");
                Predicate donePredicate = builder.greaterThan(donePath.as(Integer.class),
                        0);
                predicates.add(donePredicate);
            }
        }

        Path<Boolean> isDeletedPath = root.get("isDeleted");
        Predicate isDeletedPredicate = builder.equal(isDeletedPath.as(Boolean.class),
                false);
        predicates.add(isDeletedPredicate);
        return predicates;
    }

}