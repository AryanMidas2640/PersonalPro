package com.Address.demo.repositry;

import com.Address.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepositry extends MongoRepository<User,String> {

    Optional<User> findByUsername(String username);

    List<User> findByRoleAndOnline(String role, boolean online);}