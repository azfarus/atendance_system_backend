package com.example.atendance_system_backend.course;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Entity;
import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course , Long> {


    Optional<Course> findCourseByDepartmentAndCourseId(String department , Long courseID);
}
