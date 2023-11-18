package com.example.atendance_system_backend.attendance;

import org.checkerframework.checker.units.qual.Luminance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository  extends JpaRepository<Attendance , AttendanceIdClass> {
    List<Attendance> findAttendanceByStudentIdAndCourseHid(Long id, Long hid);

    List<Attendance> findAttendanceByCourseHid(Long hid);

    Long countAttendanceByCourseHid(Long hid);

    Long countAttendanceByCourseHidAndStatusNotContains(Long hid , String status);
    Optional<Attendance> findAttendanceByStudentIdAndCourseHidAndDate(Long id , Long hid , LocalDate Date);
}
