package com.ems.application.service.dashboard;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.dashboard.CountByDashboardCategoryResponse;
import com.ems.application.dto.dashboard.TopProductRequest;
import com.ems.application.dto.dashboard.TopProductResponse;
import com.ems.application.dto.dashboard.TotalOrderResponse;
import com.ems.application.repository.OrderDetailRepository;
import com.ems.application.repository.OrderRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.HashIdsUtils;

@Service

public class DashboardService extends BaseService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final HashIdsUtils hashIdsUtils;

    public DashboardService(
            OrderDetailRepository orderDetailRepository,
            OrderRepository orderRepository,
            HashIdsUtils hashIdsUtils) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<List<TopProductResponse>> countTopProduct(TopProductRequest topProductRequest) {
        List<TopProductResponse> result = new ArrayList<>();
        List<Object[]> orderDetails = orderDetailRepository.findTop10Products();
        for (Object[] orderDetail : orderDetails) {
            TopProductResponse topProductResponse = new TopProductResponse();
            topProductResponse.setProductName((String) orderDetail[0]);
            topProductResponse.setTotal((BigInteger) orderDetail[1]);
            result.add(topProductResponse);
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<List<TotalOrderResponse>> countTotalOrder(TopProductRequest topProductRequest) {
        List<TotalOrderResponse> result = new ArrayList<>();
        List<Object[]> temp = orderRepository.getReportByCategory();
        for (Object[] object : temp) {
            TotalOrderResponse totalOrderResponse = new TotalOrderResponse();
            totalOrderResponse.setCategoryName((String) object[0]);
            totalOrderResponse.setTotal((BigInteger) object[1]);
            result.add(totalOrderResponse);
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<List<Double>> countRevenue(TopProductRequest topProductRequest) {
        List<Double> result = new ArrayList<>(Collections.nCopies(12, 0.0));
        List<Object[]> temp = orderRepository.getReportByMonth();
        for (Object[] object : temp) {
            result.set((Integer) object[0], (Double) object[1]);
        }
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<CountByDashboardCategoryResponse> countByDashboardCategory(
            TopProductRequest topProductRequest) {
        CountByDashboardCategoryResponse result = new CountByDashboardCategoryResponse();
        List<Object[]> temp = orderRepository.getTotalOrderAndRevenue();
        result.setTotalOrder((BigInteger) temp.get(0)[0]);
        result.setRevenue((Double) temp.get(0)[1]);
        return ResponseEntity.ok(result);
    }
}