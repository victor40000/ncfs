package com.itmo.ncfs.repos;

import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.enums.FeedbackStatus;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

public interface FeedbackRepo extends CrudRepository<Feedback, Integer> {

    @Override
    List<Feedback> findAll();

    List<Feedback> findFeedbacksByCreatedBy(Integer id);
}
