package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.OrderDetail;
import com.ems.application.entity.Orders;

@Repository
public interface OrderDetailRepository extends JpaRepositoryBase<OrderDetail, Integer>,
        JpaSpecificationExecutor<OrderDetail> {
    List<OrderDetail> findByOrder(Orders order);

    List<OrderDetail> findByOrderAndStatus(Orders order, Integer status);

    OrderDetail findByOrderAndProductId(Orders order, Integer productId);

    OrderDetail findByOrderAndProductIdAndStatus(Orders order, Integer productId, Integer status);

    @Query(value = "SELECT p.name, count(od.quantity) as count from order_detail od join product p on od.product_id = p.id group by product_id order by count desc limit 10", nativeQuery = true)
    List<Object[]> findTop10Products();
}
