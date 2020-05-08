package com.itmo.ncfs.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "products")
    private List<User> purchasedBy;

    @OneToMany(mappedBy = "product")
    private List<Feedback> feedback;
}
