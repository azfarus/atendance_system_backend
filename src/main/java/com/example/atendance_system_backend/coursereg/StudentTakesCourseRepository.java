package com.example.atendance_system_backend.coursereg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentTakesCourseRepository extends JpaRepository<StudentTakesCourse, StudentCourseIdClass> {
    List<StudentTakesCourse> findAllByCourseHid(Long hid);

    void deleteStudentTakesCourseByCourseHid(Long hid);

    Long countStudentTakesCourseByCourseHid(Long hid);

    List<StudentTakesCourse> findByStudentId(Long sid);
}
