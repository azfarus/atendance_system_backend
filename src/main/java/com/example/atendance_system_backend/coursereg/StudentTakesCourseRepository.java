package com.example.atendance_system_backend.coursereg;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentTakesCourseRepository extends JpaRepository<StudentTakesCourse, StudentCourseIdClass> {

}
