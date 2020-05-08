package com.itmo.ncfs.entities;

import com.itmo.ncfs.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String login;

    private String password;

    private UserRole role;

    private boolean active;

    @OneToMany(mappedBy = "createdBy")
    private List<Feedback> createdFeedback;

    @OneToMany(mappedBy = "moderatedBy")
    private List<Feedback> moderatedFeedback;

    @ManyToMany(mappedBy = "purchasedBy")
    private List<Product> products;
}
