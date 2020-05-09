package com.itmo.ncfs.services;

import com.itmo.ncfs.entities.Product;
import com.itmo.ncfs.entities.User;
import com.itmo.ncfs.enums.UserRole;
import com.itmo.ncfs.repos.FeedbackRepo;
import com.itmo.ncfs.repos.ProductRepo;
import com.itmo.ncfs.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DBInitializer {

    private final UserRepo userRepo;
    private final FeedbackRepo feedbackRepo;
    private final ProductRepo productRepo;

    @Autowired
    public DBInitializer(UserRepo userRepo, FeedbackRepo feedbackRepo, ProductRepo productRepo) {
        this.userRepo = userRepo;
        this.feedbackRepo = feedbackRepo;
        this.productRepo = productRepo;
    }

    @PostConstruct
    public void init() {
        if (userRepo.findById(1).isPresent()) {
            return;
        }
        User user0 = new User();
        user0.setLogin("user0");
        user0.setActive(true);
        user0.setPassword("1");
        user0.setRole(UserRole.CUSTOMER);
        user0.setProducts(new ArrayList<>());
        User user1 = new User();
        user1.setLogin("user1");
        user1.setActive(true);
        user1.setPassword("1");
        user1.setRole(UserRole.CUSTOMER);
        user1.setProducts(new ArrayList<>());
        User user2 = new User();
        user2.setLogin("user2");
        user2.setActive(true);
        user2.setPassword("1");
        user2.setRole(UserRole.CUSTOMER);
        user2.setProducts(new ArrayList<>());
        User user3 = new User();
        user3.setLogin("user3");
        user3.setActive(true);
        user3.setPassword("1");
        user3.setRole(UserRole.MODERATOR);
        user3.setProducts(new ArrayList<>());
        User user4 = new User();
        user4.setLogin("user4");
        user4.setActive(true);
        user4.setPassword("1");
        user4.setRole(UserRole.MODERATOR);
        user4.setProducts(new ArrayList<>());


        Product product0 = new Product();
        product0.setName("smartphone");
        product0.setDescription("test smartphone");
        product0.setPurchasedBy(new ArrayList<>());
        Product product1 = new Product();
        product1.setName("tablet");
        product1.setDescription("test tablet");
        product1.setPurchasedBy(new ArrayList<>());

        List<User> users = Arrays.asList(user0, user1, user2, user3, user4);
        List<Product> products = Arrays.asList(product0, product1);

        productRepo.saveAll(products);
        userRepo.saveAll(users);

        user0.getProducts().add(product0);
        user1.getProducts().add(product0);
        product0.getPurchasedBy().add(user0);
        product0.getPurchasedBy().add(user1);

        productRepo.saveAll(products);
        userRepo.saveAll(users);
    }

}
