package com.example.jobTpro.service;

import com.example.jobTpro.entity.Job;
import com.example.jobTpro.entity.User;
import com.example.jobTpro.repo.JobRepository;
import com.example.jobTpro.repo.UserRepository;
import com.example.jobTpro.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Map<String, Object> createJob(String token, Job jobRequest) throws Exception {
        int userId = jwtTokenUtil.getUserIdFromToken(token);
        User createdBy = userRepository.findById(userId);

        if (createdBy == null) {
            throw new Exception("User not found");
        }

        jobRequest.setCreatedBy(createdBy);
        jobRequest.setCreatedAt(new Date());
        jobRequest.setUpdatedAt(new Date());

        Job savedJob = jobRepository.save(jobRequest);

        Map<String, Object> jobResponse = new HashMap<>();
        jobResponse.put("company", savedJob.getCompany());
        jobResponse.put("position", savedJob.getPosition());
        jobResponse.put("status", savedJob.getStatus().getValue());
        jobResponse.put("createdBy", savedJob.getCreatedBy().getId());
        jobResponse.put("jobType", savedJob.getJobType().getValue());
        jobResponse.put("jobLocation", savedJob.getJobLocation());
        jobResponse.put("_id", savedJob.get_id());
        jobResponse.put("createdAt", savedJob.getCreatedAt());
        jobResponse.put("updatedAt", savedJob.getUpdatedAt());

        return jobResponse;
    }

    public Map<String, Object> updateJob(String token, int jobId, Job jobRequest) throws Exception {
        int userId = jwtTokenUtil.getUserIdFromToken(token);
        Job existingJob = jobRepository.findById(jobId);

        if (existingJob == null) {
            throw new Exception("Job not found");
        }

        if (existingJob.getCreatedBy().getId() != userId) {
            throw new Exception("Unauthorized");
        }

        if (jobRequest.getCompany() != null) existingJob.setCompany(jobRequest.getCompany());
        if (jobRequest.getPosition() != null) existingJob.setPosition(jobRequest.getPosition());
        if (jobRequest.getStatus() != null) existingJob.setStatus(jobRequest.getStatus());
        if (jobRequest.getJobType() != null) existingJob.setJobType(jobRequest.getJobType());
        if (jobRequest.getJobLocation() != null) existingJob.setJobLocation(jobRequest.getJobLocation());
        existingJob.setUpdatedAt(new Date());

        Job updatedJob = jobRepository.save(existingJob);

        Map<String, Object> jobResponse = new HashMap<>();
        jobResponse.put("company", updatedJob.getCompany());
        jobResponse.put("position", updatedJob.getPosition());
        jobResponse.put("status", updatedJob.getStatus().getValue());
        jobResponse.put("createdBy", updatedJob.getCreatedBy().getId());
        jobResponse.put("jobType", updatedJob.getJobType().getValue());
        jobResponse.put("jobLocation", updatedJob.getJobLocation());
        jobResponse.put("_id", updatedJob.get_id());
        jobResponse.put("createdAt", updatedJob.getCreatedAt());
        jobResponse.put("updatedAt", updatedJob.getUpdatedAt());

        return jobResponse;
    }

    public Map<String, Object> deleteJob(int jobId) throws Exception {
        Job existingJob = jobRepository.findById(jobId);

        if (existingJob == null) {
            throw new Exception("Job not found");
        }

        jobRepository.delete(existingJob);

        Map<String, Object> jobResponse = new HashMap<>();
        jobResponse.put("msg","Job deleted");
        return jobResponse;
    }
}
