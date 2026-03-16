    package com.Address.demo.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.Max;
    import jakarta.validation.constraints.Min;
    import jakarta.validation.constraints.NotBlank;
    import lombok.Data;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.index.Indexed;
    import org.springframework.data.mongodb.core.mapping.Document;

   @Data
    @Document(collection = "newJob")
    @JsonIgnoreProperties(ignoreUnknown = false)
    public class Model {

        @Indexed(unique = true)
        private String jobId;


        @Id
        private String id;

        @NotBlank
        private String jobTitle;

        @NotBlank
        private String companyName;

       @NotBlank
       private String city;

       @NotBlank
       private String jobType;
        private String workMode;

        @Min(0)
        @Max(9)
        private int minExperience;

        @Min(10)
        private int maxExperience;


        private double salary;
        private String description;

        @Email
        private String email;


        public Model() {}

        public Model(String jobTitle, String companyName, String city, String jobType,
                   String workMode, int minExperience, int maxExperience,
                   double salary, String description , String jobId, String email) {
            this.jobTitle = jobTitle;
            this.companyName = companyName;
            this.city = city;
            this.jobType = jobType;
            this.workMode = workMode;
            this.minExperience = minExperience;
            this.maxExperience = maxExperience;
            this.salary = salary;
            this.description = description;
            this .jobId=jobId;
            this.email=email;
        }

    }