package com.example.jobTpro.repo;

import com.example.jobTpro.entity.Job;
import com.example.jobTpro.entity.JobStatus;
import com.example.jobTpro.entity.JobType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Integer> {
    Job findById(int id);

    @Query("SELECT j FROM Job j WHERE " +
            "(:status IS NULL OR j.status = :status) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "j.createdBy.id = :userId AND " +
            "(LOWER(j.company) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            ":search IS NULL) " +
            "ORDER BY CASE WHEN :sort = 'latest' THEN j.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN j.createdAt END ASC, " +
            "CASE WHEN :sort = 'a-z' THEN j.company END ASC, " +
            "CASE WHEN :sort = 'z-a' THEN j.company END DESC")
    List<Job> searchJobs(JobStatus status, JobType jobType, String search, String sort,int userId, Pageable pageable);
}
