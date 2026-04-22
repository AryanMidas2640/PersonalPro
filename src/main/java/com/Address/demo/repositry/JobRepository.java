package com.Address.demo.repositry;

import com.Address.demo.model.Model;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Model, String> {

    Model findByJobId(String jobId);

}