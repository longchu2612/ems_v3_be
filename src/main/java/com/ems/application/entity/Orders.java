package com.ems.application.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor

public class Orders extends EntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer tableId;
    private String note;
    private String customerName;
    private Integer status = 1;
    private Double totalPrice;
    private Integer totalQuantity;
    // @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<OrderDetail> orderDetails;
}
