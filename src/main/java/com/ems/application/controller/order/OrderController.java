package com.ems.application.controller.order;

import javax.validation.Valid;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.order.GetOrderByTable;
import com.ems.application.dto.order.MergeOrderRequest;
import com.ems.application.dto.order.NewOrderRequest;
import com.ems.application.dto.order.OrderListSearchCriteria;
import com.ems.application.dto.order.OrderResponse;
import com.ems.application.dto.order.ProductByOrderResponse;
import com.ems.application.service.order.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Order")

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @ApiOperation(value = "Confirm new order")
    @PostMapping(value = "/confirm")
    public ResponseEntity<OrderResponse> addOrder(@Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.createNewOrder(orderRequest);
    }

    @ApiOperation(value = "Get order by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable("id") String id,
            @RequestParam(value = "status", required = false) Integer status) {
        return orderService.getOrderById(id, status);
    }

    @ApiOperation(value = "Update order")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.updateOrder(id, orderRequest);
    }

    @ApiOperation(value = "Update order from cart")
    @PostMapping(value = "/add-from-cart/{id}")
    public ResponseEntity<OrderResponse> addFromCart(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.addFromCart(id, orderRequest);
    }

    @ApiOperation(value = "Delete order")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable("id") String id) {
        return orderService.deleteOrder(id);
    }

    @ApiOperation(value = "Get order by tableId")
    @PostMapping(value = "/get-by-table")
    public ResponseEntity<OrderResponse> getOrderByTableId(@Valid @RequestBody GetOrderByTable orderRequest) {
        return orderService.getOrderByTableId(orderRequest);
    }

    @ApiOperation(value = "Get all Order")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<OrderResponse>> getOrderByTableId(@Valid @RequestBody OrderListSearchCriteria criteria) {
        return orderService.getAllOrder(criteria);
    }

    @ApiOperation(value = "Get all Order")
    @PostMapping(value = "/list-current")
    public ResponseEntity<Page<OrderResponse>> getAllCurrentOrder(
            @Valid @RequestBody OrderListSearchCriteria criteria) {
        return orderService.getAllCurrentOrder(criteria);
    }

    @ApiOperation(value = "Get all Product in Order")
    @PostMapping(value = "/list-product-by-order")
    public ResponseEntity<Page<ProductByOrderResponse>> getProductByOrder(
            @Valid @RequestBody OrderListSearchCriteria criteria) {
        return orderService.getProductByOrder(criteria);
    }

    @ApiOperation(value = "Process Order")
    @PutMapping(value = "/process/{id}")
    public ResponseEntity<OrderResponse> processOrder(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.processOrder(id, orderRequest);
    }

    @ApiOperation(value = "Complete Order")
    @PutMapping(value = "/complete/{id}")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.completeOrder(id, orderRequest);
    }

    @ApiOperation(value = "Close Order")
    @PutMapping(value = "/close/{id}")
    public ResponseEntity<OrderResponse> closeOrder(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.closeOrder(id, orderRequest);
    }

    @ApiOperation(value = "Call waiter Order")
    @PutMapping(value = "/call-waiter/{id}")
    public ResponseEntity<OrderResponse> callWaiter(@PathVariable("id") String id) {
        return orderService.callWaiter(id);
    }

    @ApiOperation(value = "Split Order")
    @PostMapping(value = "/split/{id}")
    public ResponseEntity<OrderResponse> splitOrder(@PathVariable("id") String id,
            @Valid @RequestBody NewOrderRequest orderRequest) {
        return orderService.splitOrder(id, orderRequest);
    }

    @ApiOperation(value = "Merge Order")
    @PostMapping(value = "/merge/{id}")
    public ResponseEntity<OrderResponse> mergeOrder(@PathVariable("id") String id,
            @Valid @RequestBody MergeOrderRequest orderRequest) {
        return orderService.mergeOrder(id, orderRequest);
    }

    @ApiOperation(value = "Export to excel")
    @GetMapping(value = "/export/{id}")
    public ResponseEntity<Resource> exportToExcel(@PathVariable("id") String id,
            @Param("customer_name") String customerName, @Param("status") Integer status) {
        return orderService.exportToExcel(id, customerName, status);
    }
}
