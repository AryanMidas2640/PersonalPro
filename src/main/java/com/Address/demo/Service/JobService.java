package com.Address.demo.Service;

import com.Address.demo.model.Model;
import java.util.List;

public interface JobService {

    // Save a job to the default database
    Model saveJob(Model model);

    // Get the number of jobs in a city from the default database
    long getJobCountByCity(String city);

    // Get all jobs in a city from the default database
    List<Model> getJobsByCity(String city);
}
