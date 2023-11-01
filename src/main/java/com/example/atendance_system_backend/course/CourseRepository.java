package com.example.atendance_system_backend.course;


import com.example.atendance_system_backend.teacher.Teacher;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import javax.persistence.Entity;
import java.util.List;
import java.util.Optional;


public interface CourseRepository extends JpaRepository<Course , Long> {

    List<Course> findCourseByDepartmentAndCourseId(String department , Long courseID);

    Optional<Course> findCourseByDepartmentAndCourseIdAndSection(String department , Long courseID , Character sec);
    List<Course> findCoursesByDepartment(String department);

    List<Course> findCoursesByTeacher(Teacher teacher);


}
