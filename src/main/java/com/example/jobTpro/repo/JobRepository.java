package com.example.jobTpro.repo;

import com.example.jobTpro.entity.Job;
import com.example.jobTpro.entity.JobStatus;
import com.example.jobTpro.entity.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Integer> {
    Job findById(int id);

    @Query(value = "SELECT j FROM Job j WHERE " +
            "j.createdBy.id = :userId AND " +
            "(:status IS NULL OR j.status = :status) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(LOWER(j.company) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            ":search IS NULL) " +
            "ORDER BY CASE WHEN :sort = 'latest' THEN j.createdAt END DESC, " +
            "CASE WHEN :sort = 'oldest' THEN j.createdAt END ASC, " +
            "CASE WHEN :sort = 'a-z' THEN j.company END ASC, " +
            "CASE WHEN :sort = 'z-a' THEN j.company END DESC",
            countQuery = "SELECT COUNT(j) FROM Job j WHERE " +
                    "j.createdBy.id = :userId AND " +
                    "(:status IS NULL OR j.status = :status) AND " +
                    "(:jobType IS NULL OR j.jobType = :jobType) AND " +
                    "(LOWER(j.company) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    "LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                    ":search IS NULL)")
    Page<Job> searchJobs(JobStatus status, JobType jobType, String search, String sort, int userId, Pageable pageable);


    @Query(value = "SELECT j.status, COUNT(*) FROM job j WHERE j.created_by = :userId GROUP BY j.status", nativeQuery = true)
    List<Object[]> countJobsByStatus(@Param("userId") int userId);

    @Query(value = "SELECT DATE_FORMAT(j.created_at, '%b %Y') AS month, COUNT(*) " +
            "FROM job j WHERE j.created_by = :userId AND j.created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
            "GROUP BY month ORDER BY MIN(j.created_at)", nativeQuery = true)
    List<Object[]> countJobsByMonth(@Param("userId") int userId);

}
