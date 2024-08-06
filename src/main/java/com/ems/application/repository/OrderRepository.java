package com.ems.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ems.application.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepositoryBase<Orders, Integer>,
        JpaSpecificationExecutor<Orders> {
    Orders findByTableIdAndStatus(Integer tableId, Integer status);

    Orders findByTableIdAndStatusLessThan(Integer tableId, Integer status);

    @Query(value = "select od.id, p.`name`, od.note, od.quantity from order_detail od join product p on od.product_id = p.id join orders o on od.orders_id = o.id where od.status = :status and o.status = 1 WHERE o.status > 4", nativeQuery = true)
    List<Object[]> getProductByOrder(@Param("status") Integer status);

    @Query(value = "SELECT c.category_name, count(o.id) from orders o join order_detail od on o.id = od.orders_id join product p on od.product_id = p.id join category c on p.category_id = c.id GROUP by c.category_name WHERE o.status > 4", nativeQuery = true)
    List<Object[]> getReportByCategory();

    @Query(value = "SELECT MONTH(updated_at) AS month, SUM(total_price) AS total_sum FROM orders GROUP BY MONTH(updated_at) WHERE orders.status > 4", nativeQuery = true)
    List<Object[]> getReportByMonth();

    @Query(value = "SELECT count(*) as total_order, sum(orders.total_price) as revenue from orders WHERE orders.status > 4", nativeQuery = true)
    List<Object[]> getTotalOrderAndRevenue();
}
