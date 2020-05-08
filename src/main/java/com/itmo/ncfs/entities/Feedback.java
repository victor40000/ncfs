package com.itmo.ncfs.entities;

import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer rating;

    private String description;

    private Date createdWhen;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    @ManyToOne
    private User createdBy;

    @ManyToOne
    private User moderatedBy;

    @OneToMany(mappedBy = "feedback")
    private List<Transition> transitions;

    @ManyToOne
    private Product product;
}
