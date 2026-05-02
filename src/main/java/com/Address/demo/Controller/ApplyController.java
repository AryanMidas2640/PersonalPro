package com.Address.demo.Controller;

import com.Address.demo.model.Application;
import com.Address.demo.model.Model;
import com.Address.demo.model.Resume;
import com.Address.demo.repositry.ApplicationRepository;
import com.Address.demo.security.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class ApplyController {

    private final ApplicationRepository applicationRepository;
    private final JwtHelper jwtHelper;
    private final MongoTemplate mongoTemplate;

    public ApplyController(
            ApplicationRepository applicationRepository,
            JwtHelper jwtHelper,
            MongoTemplate mongoTemplate) {

        this.applicationRepository = applicationRepository;
        this.jwtHelper = jwtHelper;
        this.mongoTemplate = mongoTemplate;
    }

    // ===============================
    // APPLY JOB
    // ===============================
    @PostMapping("/apply/{jobId}/{status}")
    public String applyJob(
            @PathVariable String jobId,
            @PathVariable String status,
            HttpServletRequest request) {

        String auth = request.getHeader("Authorization");
        String token = auth.substring(7);

        String studentUsername =
                jwtHelper.getUsernameFromToken(token);

        Query jobQuery = new Query();
        jobQuery.addCriteria(
                Criteria.where("jobId").is(jobId)
        );

        Model job = mongoTemplate.findOne(
                jobQuery,
                Model.class,
                "jobs"
        );

        if (job == null) {
            return "Job Not Found";
        }

        String tenantId = job.getTenantId();

        Query resumeQuery = new Query();
        resumeQuery.addCriteria(
                Criteria.where("username").is(studentUsername)
        );

        Resume resume = mongoTemplate.findOne(
                resumeQuery,
                Resume.class,
                "resume"
        );

        if (resume == null) {
            return "Resume not found";
        }

        Application oldApp =
                applicationRepository
                        .findByStudentUsernameAndJobId(
                                studentUsername,
                                jobId
                        );

        if (oldApp != null) {

            // agar HOLD hai → update kar do
            if (oldApp.getStatus().equalsIgnoreCase("Hold")) {
                oldApp.setStatus("Applied");
                applicationRepository.save(oldApp);
                return "Re-Applied Successfully";
            }

            // agar already applied hai → block
            if (oldApp.getStatus().equalsIgnoreCase("Applied")) {
                return "Already Applied";
            }
        }

        Application app = new Application();

        app.setTenantId(tenantId);
        app.setStudentUsername(studentUsername);
        app.setStudentName(resume.getName());
        app.setResumeName(resume.getName());
        app.setEmail(resume.getEmail());

        app.setJobId(job.getJobId());
        app.setJobTitle(job.getJobTitle());
        app.setCompanyName(job.getCompanyName());

        app.setStatus(status);

        applicationRepository.save(app);

        return "Applied Successfully";
    }

    // ===============================
    // MY APPLIED JOBS
    // ===============================
    @GetMapping("/my-applied")
    public List<Application> myApplied(
            HttpServletRequest request) {

        String auth = request.getHeader("Authorization");
        String token = auth.substring(7);

        String username =
                jwtHelper.getUsernameFromToken(token);

        return applicationRepository
                .findByStudentUsername(username);
    }

    // ===============================
    // MY APPLICANTS
    // ===============================
    @GetMapping("/my-applicants")
    public List<Application> myApplicants(
            HttpServletRequest request) {

        String auth = request.getHeader("Authorization");
        String token = auth.substring(7);

        String tenant =
                jwtHelper.getTenantIdFromToken(token);

        return applicationRepository
                .findByTenantId(tenant);
    }
}