package com.ordertrack.orderdertrack.api.customer.model.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   private Long user_id;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String phone;

    @Column(nullable = false, unique = true)
    String email;


    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
