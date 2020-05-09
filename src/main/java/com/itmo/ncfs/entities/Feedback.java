package com.itmo.ncfs.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    private String declineMessage;

    private LocalDateTime createdWhen;

    private LocalDateTime updatedWhen;

    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    private Integer createdBy;

    private Integer moderatedBy;

    @OneToMany(mappedBy = "feedback")
    private List<Transition> transitions;

    private Integer productId;
}
