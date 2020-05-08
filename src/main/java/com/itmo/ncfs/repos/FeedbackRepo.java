package com.itmo.ncfs.repos;

import com.itmo.ncfs.entities.Feedback;
import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepo extends CrudRepository<Feedback, Integer> {
}
