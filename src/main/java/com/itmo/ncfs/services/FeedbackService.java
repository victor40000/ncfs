package com.itmo.ncfs.services;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.entities.Transition;
import com.itmo.ncfs.enums.FeedbackStatus;
import com.itmo.ncfs.exceptions.ValidationException;
import com.itmo.ncfs.repos.FeedbackRepo;
import com.itmo.ncfs.repos.TransitionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final String FEEDBACK_NOT_FOUND = "Feedback with id = %s not found";

    @PersistenceContext
    EntityManager entityManager;

    private final FeedbackValidator validator;
    private final FeedbackRepo feedbackRepo;
    private final TransitionRepo transitionRepo;

    @Autowired
    public FeedbackService(FeedbackValidator validator, FeedbackRepo feedbackRepo, TransitionRepo transitionRepo) {
        this.validator = validator;
        this.feedbackRepo = feedbackRepo;
        this.transitionRepo = transitionRepo;
    }

    public FeedbackDto addFeedback(FeedbackDto feedbackDto) {
        validator.validateAddFeedbackCase(feedbackDto);
        Feedback feedback = getFeedback(feedbackDto);
        feedback.setCreatedWhen(LocalDateTime.now());
        feedback.setId(null);
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        return getFeedbackDto(feedbackRepo.save(feedback));
    }

    public FeedbackDto changeFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepo.findById(feedbackDto.getId())
                .orElseThrow(() -> new ValidationException(String.format(FEEDBACK_NOT_FOUND, feedbackDto.getId())));
        validator.validateChangeFeedbackCase(feedback);
        feedback.setRating(feedbackDto.getRating());
        feedback.setDescription(feedbackDto.getDescription());
        return getFeedbackDto(feedbackRepo.save(feedback));
    }

    public FeedbackDto changeFeedbackStatus(FeedbackDto feedbackDto) {
        Feedback feedback = feedbackRepo.findById(feedbackDto.getId())
                .orElseThrow(() -> new ValidationException(String.format(FEEDBACK_NOT_FOUND, feedbackDto.getId())));
        Transition transition = new Transition();
        validator.validateChangeStatusCase(feedbackDto, feedback);
        if (FeedbackStatus.SUBMITTED.equals(feedbackDto.getStatus())) {
            feedback.setModeratedBy(null);
        }
        if (FeedbackStatus.IN_PROGRESS.equals(feedbackDto.getStatus())) {
            feedback.setModeratedBy(feedbackDto.getModeratorId());
        }
        if (FeedbackStatus.DECLINED.equals(feedbackDto.getStatus())){
            feedback.setDeclineMessage(feedbackDto.getDeclineMessage());
        }
        transition.setSource(feedback.getStatus());
        transition.setDestination(feedbackDto.getStatus());
        transition.setDate(new Timestamp(System.currentTimeMillis()));
        feedback.setStatus(feedbackDto.getStatus());
        transitionRepo.save(transition);
        return getFeedbackDto(feedbackRepo.save(feedback));
    }

    public List<FeedbackDto> getFeedback(LocalDateTime startDate,
                                         LocalDateTime endDate,
                                         List<Integer> feedbackIds,
                                         Integer moderatorId,
                                         Integer productId,
                                         List<FeedbackStatus> statuses) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Feedback> query = builder.createQuery(Feedback.class);
        Root<Feedback> feedbackRoot = query.from(Feedback.class);


        Predicate feedbackIdPred = feedbackRoot.get("id").isNotNull();
        if (feedbackIds != null) {
            feedbackIdPred = feedbackRoot.get("id").in(feedbackIds);
        }

        Predicate moderatorIdPred = feedbackRoot.get("moderatedBy").isNotNull();
        if (moderatorId != null) {
            moderatorIdPred = feedbackRoot.get("moderatedBy").in(moderatorId);
        }

        Predicate productIdPred = feedbackRoot.get("productId").isNotNull();
        if (productId != null) {
            productIdPred = feedbackRoot.get("productId").in(productId);
        }

        Predicate statusesPred = feedbackRoot.get("status").isNotNull();
        if (statuses != null) {
            statusesPred = feedbackRoot.get("status").in(statuses);
        }

        Predicate startDatePred = feedbackRoot.get("createdWhen").isNotNull();
        if (startDate != null) {
            startDatePred = builder.greaterThan(feedbackRoot.get("createdWhen"), startDate);
        }

        Predicate endDatePred = feedbackRoot.get("createdWhen").isNotNull();
        if (endDate != null) {
            endDatePred = builder.lessThan(feedbackRoot.get("createdWhen"), endDate);
        }

        if (moderatorId == null) {
            query.where(feedbackIdPred, productIdPred, statusesPred, startDatePred, endDatePred);
        } else {
            query.where(feedbackIdPred, moderatorIdPred, productIdPred, statusesPred, startDatePred, endDatePred);
        }
        return entityManager.createQuery(query).getResultList().stream()
                .map(this::getFeedbackDto)
                .collect(Collectors.toList());
    }

    public void deleteFeedback(Integer id) {
        Feedback feedback = feedbackRepo.findById(id)
                .orElseThrow(() -> new ValidationException(String.format(FEEDBACK_NOT_FOUND, id)));
        feedbackRepo.deleteById(id);
    }

    public Double getRating(Integer productId) {
        List<Feedback> feedback = feedbackRepo.findAll().stream()
                .filter(var -> productId.equals(var.getProductId()))
                .collect(Collectors.toList());
        double sum = feedback.stream()
                .map(Feedback::getRating)
                .mapToInt(Integer::intValue)
                .sum();

        return !feedback.isEmpty() ? sum/feedback.size() : 0.0;
    }

    private FeedbackDto getFeedbackDto(Feedback feedback) {
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setId(feedback.getId());
        feedbackDto.setCreatedWhen(feedback.getCreatedWhen());
        feedbackDto.setDescription(feedback.getDescription());
        feedbackDto.setRating(feedback.getRating());
        feedbackDto.setStatus(feedback.getStatus());
        feedbackDto.setCustomerId(feedback.getCreatedBy());
        feedbackDto.setModeratorId(feedback.getModeratedBy());
        feedbackDto.setProductId(feedback.getProductId());
        feedbackDto.setDeclineMessage(feedback.getDeclineMessage());
        return feedbackDto;
    }

    private Feedback getFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();
        feedback.setId(feedbackDto.getId());
        feedback.setCreatedWhen(feedbackDto.getCreatedWhen());
        feedback.setDescription(feedbackDto.getDescription());
        feedback.setRating(feedbackDto.getRating());
        feedback.setStatus(feedbackDto.getStatus());
        feedback.setProductId(feedbackDto.getProductId());
        feedback.setModeratedBy(feedbackDto.getModeratorId());
        feedback.setCreatedBy(feedbackDto.getCustomerId());
        feedback.setDeclineMessage(feedbackDto.getDeclineMessage());
        feedback.setDeclineMessage(feedbackDto.getDeclineMessage());
        return feedback;
    }
}
