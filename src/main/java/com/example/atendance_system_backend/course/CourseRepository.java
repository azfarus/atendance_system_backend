package com.example.atendance_system_backend.course;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;


public interface CourseRepository extends JpaRepository<Course , Long> {
}
