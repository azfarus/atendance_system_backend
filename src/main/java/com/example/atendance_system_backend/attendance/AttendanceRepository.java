package com.example.atendance_system_backend.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository  extends JpaRepository<Attendance , AttendanceIdClass> {
    List<Attendance> findAttendanceByStudentIdAndCourseHid(Long id, Long hid);
    Optional<Attendance> findAttendanceByStudentIdAndCourseHidAndDate(Long id , Long hid , LocalDate Date);
}
