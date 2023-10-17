package com.example.atendance_system_backend.coursereg;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentTakesCourseRepository extends JpaRepository<StudentTakesCourse, StudentCourseIdClass> {
    List<StudentTakesCourse> findAllByCourseHid(Long hid);
}
