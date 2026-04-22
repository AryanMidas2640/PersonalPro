package com.Address.demo.ServiceImp;

import com.Address.demo.Service.JobService;
import com.Address.demo.config.MongoTemplateFactory;
import com.Address.demo.model.Model;
import com.Address.demo.repositry.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final MongoTemplateFactory mongoTemplateFactory;
    private final JobRepository jobRepository;


    // Default
    private MongoTemplate fromTemplates() {
        return mongoTemplateFactory.getDefaultTemplate();
    }
    public List<Model> getAllJobs() {

        return jobRepository.findAll();
    }
    @Override
    public Model getJobById(String jobId) {

        return jobRepository.findByJobId(jobId);
    }

    // Dynamic
   /* private MongoTemplate fromTemplates(String dbKey) {
        return mongoTemplateFactory.getTemplateByKey(dbKey);
    }*/


   public JobServiceImpl(MongoTemplateFactory mongoTemplateFactory, JobRepository jobRepository) {
        this.mongoTemplateFactory = mongoTemplateFactory;
       this.jobRepository = jobRepository;
   }

   // Get MongoTemplate for default DB defined in application.properties
   /* private MongoTemplate fromTemplate() {

        return mongoTemplateFactory.getTemplates();
    }*/


    @Override
    public Model saveJob(Model model) {
       // log.info("Saving jobId={} to default database", model.getJobId());

        MongoTemplate templates = fromTemplates();

        // Check if jobId already exists
        Query query = Query.query(Criteria.where("jobId").is(model.getJobId()));
        boolean exists = templates.exists(query, Model.class, "newJob");

        if (exists) {
            //log.warn("Job with jobId={} already exists. Skipping save.", model.getJobId());
            throw new IllegalArgumentException("Job with jobId " + model.getJobId() + " already exists!");
        }

        templates.save(model, "newJob");

        //log.info("Saved jobId={} to default database", model.getJobId());
        return model;
    }


    @Override
    public List<Model> getJobsByCity(String city) {
        log.info("Fetching jobs for city={} from default database", city);

        MongoTemplate template = fromTemplates();

        Query query = Query.query(Criteria.where("city").is(city));

        return template.find(query, Model.class, "newJob");
    }

    @Override
    public long getJobCountByCity(String city) {
        log.info("Counting jobs for city={} from default database", city);

        MongoTemplate template = fromTemplates();

        Query query = Query.query(Criteria.where("city").is(city));

        return template.count(query, "newJob");
    }
}
