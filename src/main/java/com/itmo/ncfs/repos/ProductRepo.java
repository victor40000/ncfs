package com.itmo.ncfs.repos;

import com.itmo.ncfs.entities.Feedback;
import com.itmo.ncfs.entities.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepo extends CrudRepository<Product, Integer> {
}
