package com.itmo.ncfs.entities;

import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(length = 512)
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
