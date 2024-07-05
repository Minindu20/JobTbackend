package com.example.jobTpro.repo;

import com.example.jobTpro.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job,Integer> {
    Job findById(int id);
}
