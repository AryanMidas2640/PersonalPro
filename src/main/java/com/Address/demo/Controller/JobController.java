package com.Address.demo.Controller;
import com.Address.demo.Service.JobService;
import com.Address.demo.dto.ApiResponse;
import com.Address.demo.model.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {


    this.jobService = jobService;

    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Model>> addJob(@Valid @RequestBody Model model) {

        Model savedJob = jobService.saveJob(model);

        ApiResponse<Model> response =
                new ApiResponse<>("success", "Job added successfully", savedJob);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /*public Model addJob(@RequestBody Model model) {
        try {
            return jobService.saveJob(model);
        } catch (IllegalArgumentException e) {
            // Agar jobId exist, bhej 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job id already present");
        }
    }

     */

    @PostMapping(value = "/add-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Model addJobFromFile(@RequestPart("file") MultipartFile file) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Model model = objectMapper.readValue(file.getInputStream(), Model.class);

        return jobService.saveJob(model);  // Uses default DB
    }

    @GetMapping("/count/{city}")
    public String getJobCountByCity(@PathVariable String city) {
        long count = jobService.getJobCountByCity(city);  // Uses default DB
        return "Total jobs in " + city + " : " + count;
    }


    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<Model>>> getJobsByCity(@PathVariable String city) {

        List<Model> jobs = jobService.getJobsByCity(city);

        ApiResponse<List<Model>> response =
                new ApiResponse<>("success", "Jobs fetched successfully", jobs);

        return ResponseEntity.ok(response);
    }
    /*public List<Model> getJobsByCity(@PathVariable String city) {
        return jobService.getJobsByCity(city);  // Uses default DB
    }

     */

    @GetMapping("/test")
    public String test() {


        return "API WORKING";

    }
}
