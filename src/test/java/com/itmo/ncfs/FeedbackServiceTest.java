package com.itmo.ncfs;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.entities.Transition;
import com.itmo.ncfs.enums.FeedbackStatus;
import com.itmo.ncfs.exceptions.ShoppingPortalException;
import com.itmo.ncfs.exceptions.ValidationException;
import com.itmo.ncfs.integrations.notification.NotificationRestClient;
import com.itmo.ncfs.integrations.shopping.ShoppingRestClient;
import com.itmo.ncfs.repos.FeedbackRepo;
import com.itmo.ncfs.repos.TransitionRepo;
import com.itmo.ncfs.services.FeedbackService;
import com.itmo.ncfs.services.FeedbackValidator;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
class FeedbackServiceTest {

    @Getter
    private Integer currId = 0;

    @Autowired
    public FeedbackService feedbackService;

    @Autowired
    public FeedbackValidator feedbackValidator;

    @MockBean
    public FeedbackRepo feedbackRepo;

    @MockBean
    public TransitionRepo transitionRepo;

    @MockBean
    public NotificationRestClient notificationRestClient;

    @MockBean
    public ShoppingRestClient shoppingRestClient;

    @BeforeEach
    public void init() {
        when(shoppingRestClient.getCustomer(any())).thenThrow(new ShoppingPortalException(""));
        when(shoppingRestClient.getProduct(any())).thenThrow(new ShoppingPortalException(""));
        doNothing().when(notificationRestClient).sendNotification(any(), any(), any());
        when(transitionRepo.save(any())).thenReturn(new Transition());
        when(feedbackRepo.save(any(Feedback.class))).thenAnswer(i -> {
            Feedback feedback = (Feedback) i.getArguments()[0];
            if (feedback.getId() == null || feedback.getId() > getCurrId()) {
                feedback.setId(getNextId());
            }
            return feedback;
        });
    }

    @Test
    void testAddFeedback() {
        // test immediately published feedback
        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setRating(3);
        feedbackDto.setCustomerId(11);
        feedbackDto.setProductId(22);
        FeedbackDto result = feedbackService.addFeedback(feedbackDto);
        Assertions.assertEquals(FeedbackStatus.APPROVED, result.getStatus());
        Assertions.assertNotNull(result.getId());

        // test not immediately published feedback
        feedbackDto.setDescription("some description");
        result = feedbackService.addFeedback(feedbackDto);
        Assertions.assertEquals(FeedbackStatus.SUBMITTED, result.getStatus());
        Assertions.assertNotNull(result.getId());
    }

    @Test
    void testChangeFeedbackStatus() {
        // test status transitions positive
        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        when(feedbackRepo.findById(any())).thenReturn(Optional.of(feedback));

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setModeratorId(33);
        feedbackDto.setStatus(FeedbackStatus.IN_PROGRESS);

        FeedbackDto result = feedbackService.changeFeedbackStatus(feedbackDto);
        Assertions.assertEquals(FeedbackStatus.IN_PROGRESS, result.getStatus());
        Assertions.assertEquals(33, result.getModeratorId());

        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setModeratedBy(33);
        feedbackDto.setStatus(FeedbackStatus.APPROVED);
        result = feedbackService.changeFeedbackStatus(feedbackDto);
        Assertions.assertEquals(FeedbackStatus.APPROVED, result.getStatus());

        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setModeratedBy(33);
        feedbackDto.setStatus(FeedbackStatus.DECLINED);
        feedbackDto.setDeclineMessage("message");
        result = feedbackService.changeFeedbackStatus(feedbackDto);
        Assertions.assertEquals(FeedbackStatus.DECLINED, result.getStatus());
        Assertions.assertEquals("message", result.getDeclineMessage());
    }

    @Test
    void testChangeFeedbackStatusNegative() {
        // test status transitions
        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        when(feedbackRepo.findById(any())).thenReturn(Optional.of(feedback));

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setModeratorId(33);
        feedbackDto.setStatus(FeedbackStatus.APPROVED);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.changeFeedbackStatus(feedbackDto));

        feedbackDto.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setStatus(FeedbackStatus.DECLINED);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.changeFeedbackStatus(feedbackDto));

        // test moderator id
        feedbackDto.setStatus(FeedbackStatus.APPROVED);
        feedbackDto.setModeratorId(2);
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setModeratedBy(1);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.changeFeedbackStatus(feedbackDto));

        //test decline message
        feedbackDto.setStatus(FeedbackStatus.DECLINED);
        feedbackDto.setModeratorId(2);
        feedback.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback.setModeratedBy(2);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.changeFeedbackStatus(feedbackDto));
    }

    @Test
    void testChangeFeedback() {
        // test available change
        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        when(feedbackRepo.findById(any())).thenReturn(Optional.of(feedback));

        FeedbackDto feedbackDto = new FeedbackDto();
        feedbackDto.setDescription("new");
        FeedbackDto result = feedbackService.changeFeedback(feedbackDto);
        Assertions.assertEquals("new", result.getDescription());

        // test available change negative
        feedback.setStatus(FeedbackStatus.DECLINED);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.changeFeedback(feedbackDto));
    }

    @Test
    void testDeleteFeedback() {
        // test available change
        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.SUBMITTED);
        when(feedbackRepo.findById(any())).thenReturn(Optional.of(feedback));
        doNothing().when(feedbackRepo).deleteById(any());

        Assertions.assertDoesNotThrow(() -> feedbackService.deleteFeedback(1));

        // test available change negative
        feedback.setStatus(FeedbackStatus.DECLINED);
        Assertions.assertThrows(ValidationException.class, () -> feedbackService.deleteFeedback(1));
    }

    @Test
    void testGetRating() {
        // test rating
        Feedback feedback = new Feedback();
        feedback.setStatus(FeedbackStatus.APPROVED);
        feedback.setProductId(1);
        feedback.setRating(5);

        Feedback feedback1 = new Feedback();
        feedback1.setStatus(FeedbackStatus.APPROVED);
        feedback1.setProductId(1);
        feedback1.setRating(3);

        Feedback feedback2 = new Feedback();
        feedback2.setStatus(FeedbackStatus.IN_PROGRESS);
        feedback2.setProductId(1);
        feedback2.setRating(1);

        Feedback feedback3 = new Feedback();
        feedback3.setStatus(FeedbackStatus.APPROVED);
        feedback3.setProductId(2);
        feedback3.setRating(5);

        Feedback feedback4 = new Feedback();
        feedback4.setStatus(FeedbackStatus.SUBMITTED);
        feedback4.setProductId(1);
        feedback4.setRating(1);

        when(feedbackRepo.findAll()).thenReturn(Arrays.asList(feedback, feedback1, feedback2, feedback3, feedback4));

        Assertions.assertEquals(4, feedbackService.getRating(1));
        Assertions.assertEquals(5, feedbackService.getRating(2));
    }

    private Integer getNextId() {
        this.currId = this.currId + 1;
        return this.currId;
    }
}
