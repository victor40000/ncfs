package com.itmo.ncfs.services;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.enums.FeedbackStatus;
import com.itmo.ncfs.exceptions.ValidationException;
import com.itmo.ncfs.repos.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeedbackValidator {

    private final FeedbackRepo feedbackRepo;

    @Autowired
    public FeedbackValidator(FeedbackRepo feedbackRepo) {
        this.feedbackRepo = feedbackRepo;
    }

    private static final String INVALID_STATUS_TRANSITION_ERROR = "It is forbidden to change status from %s to %s";
    private static final String INVALID_MODERATOR_ERROR = "It is forbidden to change status for moderatorId: %s; expected value: %s";
    private static final String FEEDBACK_ALREADY_EXISTS_ERROR = "Feedback for productId: %s already created by customer with id: %s";
    private static final String FEEDBACK_COULD_NOT_BE_CHANGED_ERROR = "Feedback: %s with status: %s could not be changed";
    private static final String EMPTY_DECLINE_MESSAGE_ERROR = "Decline reason could not be blank";

    private final Map<FeedbackStatus, List<FeedbackStatus>> statusTransitions = new HashMap<>() {{
        put(FeedbackStatus.SUBMITTED, Arrays.asList(FeedbackStatus.IN_PROGRESS, FeedbackStatus.SUBMITTED));
        put(FeedbackStatus.IN_PROGRESS, Arrays.asList(FeedbackStatus.IN_PROGRESS, FeedbackStatus.SUBMITTED,
                FeedbackStatus.APPROVED, FeedbackStatus.DECLINED));
        put(FeedbackStatus.APPROVED, new ArrayList<>());
        put(FeedbackStatus.DECLINED, new ArrayList<>());
    }};

    public void validateAddFeedbackCase(FeedbackDto dto) {
        boolean errorResult = feedbackRepo.findFeedbacksByCreatedBy(dto.getCustomerId()).stream()
                .filter(var -> !FeedbackStatus.DECLINED.equals(var.getStatus()))
                .anyMatch(var -> Objects.equals(var.getProductId(), dto.getProductId()));
        if(errorResult) {
            throw new ValidationException(String.format(FEEDBACK_ALREADY_EXISTS_ERROR,
                    dto.getProductId(), dto.getCustomerId()));
        }
    }

    public void validateChangeFeedbackCase(Feedback feedback) {
        if (!FeedbackStatus.SUBMITTED.equals(feedback.getStatus())) {
            throw new ValidationException(String.format(FEEDBACK_COULD_NOT_BE_CHANGED_ERROR,
                    feedback.getId(), feedback.getStatus()));
        }
    }

    public void validateChangeStatusCase(FeedbackDto dto, Feedback feedback) {
        validateStatusTransition(feedback.getStatus(), dto.getStatus());
        if(FeedbackStatus.SUBMITTED.equals(feedback.getStatus())) {
            return;
        }
        if (!Objects.equals(feedback.getModeratedBy(), dto.getModeratorId())) {
            throw new ValidationException(String.format(INVALID_MODERATOR_ERROR,
                    dto.getModeratorId(), feedback.getModeratedBy()));
        }
        if (FeedbackStatus.DECLINED.equals(dto.getStatus()) &&
                (dto.getDeclineMessage() == null || dto.getDeclineMessage().isBlank())) {
            throw new ValidationException(EMPTY_DECLINE_MESSAGE_ERROR);
        }
    }

    private void validateStatusTransition(FeedbackStatus source, FeedbackStatus destination) {
        if (statusTransitions.get(source).contains(destination)) {
            return;
        }
        throw new ValidationException(String.format(INVALID_STATUS_TRANSITION_ERROR, source, destination));
    }



}
