package com.example.jobTpro.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="job")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int _id;
    private String company;
    private String position;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private String jobLocation;
    private Date createdAt;
    private Date updatedAt;

}
