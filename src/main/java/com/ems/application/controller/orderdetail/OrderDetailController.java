package com.ems.application.controller.orderdetail;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.base.BaseResponse;
import com.ems.application.dto.order.OrderListSearchCriteria;
import com.ems.application.dto.orderdetail.OrderDetailResponse;
import com.ems.application.dto.orderdetail.ProcessOrderDetail;
import com.ems.application.service.orderdetail.OrderDetailService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Order Detail")

@RestController
@RequestMapping(value = "/api/order-detail")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @ApiOperation(value = "Get all Order Detail")
    @PostMapping(value = "/list")
    public ResponseEntity<Page<OrderDetailResponse>> getOrderByTableId(
            @Valid @RequestBody OrderListSearchCriteria criteria) {
        return orderDetailService.getAllOrderDetail(criteria);
    }

    @ApiOperation(value = "pend")
    @PutMapping(value = "/pend/{id}")
    public ResponseEntity<BaseResponse> pendOrderDetail(@PathVariable("id") String id) {
        return orderDetailService.pendOrderDetail(id);
    }

    @ApiOperation(value = "process")
    @PutMapping(value = "/process/{id}")
    public ResponseEntity<BaseResponse> processOrderDetail(@PathVariable("id") String id,
            @Valid @RequestBody ProcessOrderDetail processOrderDetai) {
        return orderDetailService.processOrderDetail(id,processOrderDetai);
    }

    @ApiOperation(value = "serve")
    @PutMapping(value = "/serve/{id}")
    public ResponseEntity<BaseResponse> ServeOrderDetail(@PathVariable("id") String id,
            @Valid @RequestBody ProcessOrderDetail processOrderDetail) {
        return orderDetailService.serveOrderDetail(id, processOrderDetail);
    }

    @ApiOperation(value = "un-serve")
    @PutMapping(value = "/un-serve/{id}")
    public ResponseEntity<BaseResponse> UnServeOrderDetail(@PathVariable("id") String id,
            @Valid @RequestBody ProcessOrderDetail processOrderDetail) {
        return orderDetailService.unServeOrderDetail(id, processOrderDetail);
    }
    @ApiOperation(value = "served")
    @PutMapping(value = "/served/{id}")
    public ResponseEntity<BaseResponse> ServedOrderDetail(@PathVariable("id") String id,
            @Valid @RequestBody ProcessOrderDetail processOrderDetail) {
        return orderDetailService.servedOrderDetail(id, processOrderDetail);
    }
}
