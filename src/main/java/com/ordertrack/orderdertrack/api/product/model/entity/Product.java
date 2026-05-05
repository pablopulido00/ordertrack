package com.ordertrack.orderdertrack.api.product.model.entity;


import com.ordertrack.orderdertrack.api.orderline.model.entity.OrderLine;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean active;

    @OneToOne(mappedBy = "product")
    private OrderLine orderLine;

    public Product(String name, BigDecimal price, Integer stock, Boolean active) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.active = active;
    }
}
