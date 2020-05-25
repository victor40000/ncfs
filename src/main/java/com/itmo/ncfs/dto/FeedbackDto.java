package com.itmo.ncfs.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itmo.ncfs.dto.cases.NewCase;
import com.itmo.ncfs.dto.cases.UpdateCase;
import com.itmo.ncfs.dto.cases.UpdateStatusCase;
import com.itmo.ncfs.enums.FeedbackStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackDto {

    public static final String MANDATORY_FIELD_ERROR = "is mandatory";
    public static final String INCORRECT_RATING_RANGE_ERROR = "rating should be a number in range [1, 5]";
    public static final String MAX_DESCRIPTION_SIZE_ERROR = "max description length is 512";

    private Integer id;

    @NotNull(groups = {NewCase.class, UpdateCase.class}, message = MANDATORY_FIELD_ERROR)
    @Range(min = 1, max = 5, groups = {NewCase.class, UpdateCase.class}, message = INCORRECT_RATING_RANGE_ERROR)
    private Integer rating;

    @Max(value = 512, message = MAX_DESCRIPTION_SIZE_ERROR)
    private String description;

    private String declineMessage;

    private LocalDateTime createdWhen;

    private LocalDateTime updatedWhen;

    @NotNull(groups = {UpdateStatusCase.class}, message = MANDATORY_FIELD_ERROR)
    private FeedbackStatus status;

    @NotNull(groups = {NewCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer customerId;

    @NotNull(groups = {UpdateStatusCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer moderatorId;

    @NotNull(groups = {NewCase.class}, message = MANDATORY_FIELD_ERROR)
    private Integer productId;
}
