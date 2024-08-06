package com.ems.application.mapping.order;

import com.ems.application.dto.order.NewOrderRequest;
import com.ems.application.dto.order.OrderResponse;
import com.ems.application.entity.Orders;
import com.ems.application.util.DateTimeHelper;
import com.ems.application.util.HashIdsUtils;

public class OrderMapping {
    public static Orders convertToEntity(NewOrderRequest orderRequest, Orders dtbOrder, HashIdsUtils hashIdsUtils) {
        dtbOrder.setCustomerName(orderRequest.getCustomerName());
        dtbOrder.setStatus(orderRequest.getStatus());
        dtbOrder.setTableId(hashIdsUtils.decodeId(orderRequest.getTableId()));
        dtbOrder.setStatus(orderRequest.getStatus());
        dtbOrder.setNote(orderRequest.getNote());
        return dtbOrder;
    }

    public static OrderResponse convertToDto(Orders dtbOrder, HashIdsUtils hashIdsUtils) {
        OrderResponse response = new OrderResponse();
        response.setId(hashIdsUtils.encodeId(dtbOrder.getId()));
        response.setTableId(dtbOrder.getTableId());
        response.setCustomerName(dtbOrder.getCustomerName());
        response.setStatus(dtbOrder.getStatus());
        response.setTotalPrice(dtbOrder.getTotalPrice());
        response.setTotalQuantity(dtbOrder.getTotalQuantity());
        response.setCreatedAt(DateTimeHelper.convertLocalDateTimeToFormattedString(dtbOrder.getCreatedAt()));
        return response;
    }

}
