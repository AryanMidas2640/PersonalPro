package com.Address.demo.Controller;

import com.Address.demo.Service.ResumeService;
import com.Address.demo.dto.ApiResponse;
import com.Address.demo.dto.ResumeResponse;

import com.sun.net.httpserver.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/parse")
    public ApiResponse parseResume(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("skills")
            String skills,

            @RequestParam("education")
            String education
    ) {

        ResumeResponse data =
                (ResumeResponse) resumeService.parseResume(
                        file,
                        skills,
                        education
                );

        return new ApiResponse(
                "Success",
                "Resume Parsed Successfully",
                data
        );
    }
}