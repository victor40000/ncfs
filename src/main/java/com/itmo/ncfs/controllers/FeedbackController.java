package com.itmo.ncfs.controllers;

import com.itmo.ncfs.entities.User;
import com.itmo.ncfs.enums.UserRole;
import com.itmo.ncfs.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FeedbackController {

    private final UserRepo userRepo;

    @Autowired
    public FeedbackController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @RequestMapping(value = "/feedback/{login}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<User> hello(@PathVariable String login){
        User user = new User();
        user.setLogin(login);
        user.setPassword("1");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        userRepo.save(user);
        return userRepo.findAll();
    }
}