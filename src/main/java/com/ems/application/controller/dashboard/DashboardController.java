package com.ems.application.controller.dashboard;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.dashboard.CountByDashboardCategoryResponse;
import com.ems.application.dto.dashboard.TopProductRequest;
import com.ems.application.dto.dashboard.TopProductResponse;
import com.ems.application.dto.dashboard.TotalOrderResponse;
import com.ems.application.service.dashboard.DashboardService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Dashboard")

@RestController
@RequestMapping(value = "/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @ApiOperation(value = "Top products")
    @PostMapping(value = "/top-product")
    public ResponseEntity<List<TopProductResponse>> countTopProduct(
            @Valid @RequestBody TopProductRequest dashboardShipmentRequest) {
        return dashboardService.countTopProduct(dashboardShipmentRequest);
    }

    @ApiOperation(value = "Total order")
    @PostMapping(value = "/total-order")
    public ResponseEntity<List<TotalOrderResponse>> countTotalOrder(
            @Valid @RequestBody TopProductRequest dashboardShipmentRequest) {
        return dashboardService.countTotalOrder(dashboardShipmentRequest);
    }

    @ApiOperation(value = "Revenue")
    @PostMapping(value = "/revenue")
    public ResponseEntity<List<Double>> countRevenue(
            @Valid @RequestBody TopProductRequest dashboardShipmentRequest) {
        return dashboardService.countRevenue(dashboardShipmentRequest);
    }

    @ApiOperation(value = "Count by dashboard category")
    @PostMapping(value = "/count-by-dashboard-category")
    public ResponseEntity<CountByDashboardCategoryResponse> countByDashboardCategory(
            @Valid @RequestBody TopProductRequest dashboardShipmentRequest) {
        return dashboardService.countByDashboardCategory(dashboardShipmentRequest);
    }
}
