package com.itmo.ncfs.entities;

import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@Entity
public class Transition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus source;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus destination;

    private Date date;

    @ManyToOne
    private Feedback feedback;
}
