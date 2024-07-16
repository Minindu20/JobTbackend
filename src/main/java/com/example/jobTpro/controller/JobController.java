package com.example.jobTpro.controller;

import com.example.jobTpro.entity.Job;
import com.example.jobTpro.entity.JobStatus;
import com.example.jobTpro.entity.JobType;
import com.example.jobTpro.repo.JobRepository;
import com.example.jobTpro.service.JobService;
import com.example.jobTpro.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JobRepository jobRepository;

    @GetMapping()
    public ResponseEntity<Map<String, List<Job>>> getAllJobs(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String jobType,
            @RequestParam String sort,
            @RequestParam int page) {

            JobStatus jobStatus = "all".equalsIgnoreCase(status) ? null : JobStatus.fromValue(status);
            JobType type = "all".equalsIgnoreCase(jobType) ? null : JobType.fromValue(jobType);
            String authToken = token.substring(7);
            Map<String, List<Job>> jobs = jobService.getAllJobs(search, jobStatus, type, sort, page,authToken);
            return ResponseEntity.ok(jobs);

    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String,Object>>getStats(@RequestHeader("Authorization") String token){
        String authToken = token.substring(7);
        Map<String, Object> response = new HashMap<>();

        Map<String, Integer> defaultStats = new HashMap<>();
        defaultStats.put("pending", 28);
        defaultStats.put("interview", 25);
        defaultStats.put("declined", 22);

        List<Map<String, Object>> monthlyApplications = new ArrayList<>();
        monthlyApplications.add(Map.of("date", "Mar 2022", "count", 8));
        monthlyApplications.add(Map.of("date", "Apr 2022", "count", 6));
        monthlyApplications.add(Map.of("date", "May 2022", "count", 4));
        monthlyApplications.add(Map.of("date", "Jun 2022", "count", 5));
        monthlyApplications.add(Map.of("date", "Jul 2022", "count", 6));
        monthlyApplications.add(Map.of("date", "Aug 2022", "count", 5));

        response.put("defaultStats", defaultStats);
        response.put("monthlyApplications", monthlyApplications);

        return ResponseEntity.ok(response);

    }



    @PostMapping()
    public ResponseEntity<Map<String, Object>> createJob(@RequestHeader("Authorization") String token, @RequestBody Job job) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("msg", "Invalid Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String authToken = token.substring(7);
            Map<String, Object> jobResponse = jobService.createJob(authToken, job);
            Map<String, Object> response = new HashMap<>();
            response.put("job", jobResponse);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @PatchMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJob(@RequestHeader("Authorization") String token, @PathVariable int jobId, @RequestBody Job jobRequest) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("msg", "Invalid Token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String authToken = token.substring(7);
            Map<String, Object> jobResponse = jobService.updateJob(authToken, jobId, jobRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("job", jobResponse);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJob(@RequestHeader("Authorization") String token, @PathVariable int jobId) {
        try {
            Map<String, Object> jobResponse = jobService.deleteJob(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(jobResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
