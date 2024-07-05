package com.example.jobTpro.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String name;
    private String lastName;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String location;
    @Transient
    private String token;
}
