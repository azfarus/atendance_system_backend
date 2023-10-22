package com.example.atendance_system_backend.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository  extends JpaRepository<Attendance , AttendanceIdClass> {
    List<Attendance> findAttendanceByStudentId(Long id);
}
