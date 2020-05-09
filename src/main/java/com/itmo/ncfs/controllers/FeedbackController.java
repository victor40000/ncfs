package com.itmo.ncfs.controllers;

import com.itmo.ncfs.dto.FeedbackDto;
import com.itmo.ncfs.dto.cases.NewCase;
import com.itmo.ncfs.dto.cases.UpdateCase;
import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.entities.User;
import com.itmo.ncfs.enums.FeedbackStatus;
import com.itmo.ncfs.enums.UserRole;
import com.itmo.ncfs.repos.FeedbackRepo;
import com.itmo.ncfs.repos.ProductRepo;
import com.itmo.ncfs.repos.UserRepo;
import com.itmo.ncfs.services.DBInitializer;
import com.itmo.ncfs.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/api")
public class FeedbackController {

    private final UserRepo userRepo;
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(UserRepo userRepo, FeedbackService feedbackService) {
        this.userRepo = userRepo;
        this.feedbackService = feedbackService;
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public FeedbackDto addFeedback(@Validated(NewCase.class) @RequestBody FeedbackDto feedback) {
        return feedbackService.addFeedback(feedback);
    }

    @RequestMapping(value = "/feedback/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public void deleteFeedback(@PathVariable Integer id) {
        feedbackService.deleteFeedback(id);
    }

    @RequestMapping(value = "/feedback/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public FeedbackDto changeFeedback(@Validated(UpdateCase.class) @RequestBody FeedbackDto feedback,
                                      @PathVariable Integer id) {
        feedback.setId(id);
        return feedbackService.changeFeedback(feedback);
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<FeedbackDto> getFeedback(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                         @RequestParam(required = false) List<Integer> ids,
                                         @RequestParam(required = false) Integer moderatorId,
                                         @RequestParam(required = false) Integer productId,
                                         @RequestParam(required = false) List<FeedbackStatus> statuses) {
        return feedbackService.getFeedback(startDate, endDate, ids, moderatorId, productId, statuses);
    }

    @RequestMapping(value = "/rating/{productId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public Double getFeedback(@PathVariable Integer productId) {
        return feedbackService.getRating(productId);
    }


    @RequestMapping(value = "/register/{login}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<User> hello(@PathVariable String login) {
        User user = new User();
        user.setLogin(login);
        user.setPassword("1");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        userRepo.save(user);
        return userRepo.findAll();
    }
}