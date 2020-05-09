package com.itmo.ncfs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itmo.ncfs.dto.cases.NewCase;
import com.itmo.ncfs.dto.cases.UpdateCase;
import com.itmo.ncfs.dto.cases.UpdateStatusCase;
import com.itmo.ncfs.entities.Product;
import com.itmo.ncfs.entities.Transition;
import com.itmo.ncfs.entities.User;
import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackDto {

    public static final String MANDATORY_FIELD_ERROR = "is mandatory";
    public static final String INCORRECT_RATING_RANGE_ERROR = "rating should be a number in range [1, 5]";

    private Integer id;

    @NotNull(groups = {NewCase.class, UpdateCase.class}, message = MANDATORY_FIELD_ERROR)
    @Range(min = 1, max = 5, groups = {NewCase.class, UpdateCase.class}, message = INCORRECT_RATING_RANGE_ERROR)
    private Integer rating;

    private String description;

    private String declineMessage;

    private LocalDateTime createdWhen;

    @NotNull(groups = {UpdateStatusCase.class}, message = MANDATORY_FIELD_ERROR)
    private FeedbackStatus status;

    @NotNull(groups = {NewCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer customerId;

    @NotNull(groups = {UpdateStatusCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer moderatorId;

    @NotNull(groups = {NewCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer productId;
}
