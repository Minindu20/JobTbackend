package com.example.jobTpro.repo;

import com.example.jobTpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findById(int id);
    User findByEmail(String email);
}
