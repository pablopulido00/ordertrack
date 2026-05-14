package com.ordertrack.orderdertrack.api.orderline.model.entity;


import com.ordertrack.orderdertrack.api.order.model.entity.Order;
import com.ordertrack.orderdertrack.api.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)


@Entity
@Table(name = "order_lines")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 150)
    private String name;

    @Column(name = "unit_price", nullable = false, scale = 10, precision = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, scale = 10, precision = 2)
    private BigDecimal lineTotal;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;


    public OrderLine(Order order, Product product, Integer quantity) {
        this.order = order;
        this.product = product;
        this.name = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
        this.lineTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));

    }
}

