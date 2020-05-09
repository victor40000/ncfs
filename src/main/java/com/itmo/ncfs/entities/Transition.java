package com.itmo.ncfs.entities;

import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

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

    private Timestamp date;

    private Integer moderatorId;

    @ManyToOne
    private Feedback feedback;
}
