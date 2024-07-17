package com.example.jobTpro.service;

import com.example.jobTpro.entity.Job;
import com.example.jobTpro.entity.JobStatus;
import com.example.jobTpro.entity.JobType;
import com.example.jobTpro.entity.User;
import com.example.jobTpro.repo.JobRepository;
import com.example.jobTpro.repo.UserRepository;
import com.example.jobTpro.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Map<String, Object> getAllJobs(String search, JobStatus searchStatus, JobType searchType, String sort, int page, String token) {
        int userId = jwtTokenUtil.getUserIdFromToken(token);
        Pageable pageable = PageRequest.of(page - 1, 10); // Assuming 10 items per page
        search = (search == null || search.isEmpty()) ? null : search;

        Page<Job> jobPage = jobRepository.searchJobs(searchStatus, searchType, search, sort, userId, pageable);
        List<Job> jobs = jobPage.getContent();
        int totalJobs = (int) jobPage.getTotalElements();
        int numOfPages = jobPage.getTotalPages();

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobs);
        response.put("totalJobs", totalJobs);
        response.put("numOfPages", numOfPages);
        return response;
    }

    public Map<String, Object> getStats(String token) {
        int userId = jwtTokenUtil.getUserIdFromToken(token);

        List<Object[]> statusCounts = jobRepository.countJobsByStatus(userId);
        Map<String, Integer> defaultStats = new HashMap<>();
        for (Object[] statusCount : statusCounts) {
            String status = (String) statusCount[0];
            Long count = ((Number) statusCount[1]).longValue();
            defaultStats.put(status.toLowerCase(), count.intValue());
        }

        List<Object[]> monthlyCounts = jobRepository.countJobsByMonth(userId);
        List<Map<String, Object>> monthlyApplications = new ArrayList<>();
        for (Object[] monthlyCount : monthlyCounts) {
            String month = (String) monthlyCount[0];
            Long count = ((Number) monthlyCount[1]).longValue();
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("date", month);
            monthData.put("count", count.intValue());
            monthlyApplications.add(monthData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("defaultStats", defaultStats);
        response.put("monthlyApplications", monthlyApplications);

        return response;
    }
}
