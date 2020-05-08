package com.itmo.ncfs.repos;

import com.itmo.ncfs.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Integer> {
    @Override
    List<User> findAll();
}
