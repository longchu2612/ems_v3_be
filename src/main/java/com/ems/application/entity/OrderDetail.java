package com.ems.application.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders order;
    private String note;
    private Integer productId;
    private Integer quantity;
    private Double price;
    private Integer done = 0;
    private Integer inProgress = 0;
    private Integer pay = 0;
    private Integer serving = 0;
    private Integer pending = 0;
    private Integer status = 1;
}
