package com.Address.demo.Service;

//package Service;

//import dto.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeService<ResumeResponse> {

    ResumeResponse parseResume(
            MultipartFile file,
            String skills,
            String education
    );
}