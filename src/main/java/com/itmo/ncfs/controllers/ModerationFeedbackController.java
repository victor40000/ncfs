package com.itmo.ncfs.controllers;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.dto.cases.UpdateStatusCase;
import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.entities.User;
import com.itmo.ncfs.enums.UserRole;
import com.itmo.ncfs.repos.UserRepo;
import com.itmo.ncfs.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/moderation")
public class ModerationFeedbackController {

    private final UserRepo userRepo;
    private final FeedbackService feedbackService;

    @Autowired
    public ModerationFeedbackController(UserRepo userRepo, FeedbackService feedbackService) {
        this.userRepo = userRepo;
        this.feedbackService = feedbackService;
    }

    @RequestMapping(value = "/feedback/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public FeedbackDto changeFeedback(@Validated(UpdateStatusCase.class) @RequestBody FeedbackDto feedback,
                                      @PathVariable Integer id){
        feedback.setId(id);
        return feedbackService.changeFeedbackStatus(feedback);
    }
}