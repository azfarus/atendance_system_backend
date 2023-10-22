package com.example.atendance_system_backend.attendance;

import com.example.atendance_system_backend.course.Course;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;



public class AttendanceIdClass implements Serializable {
    private Long studentId;

    private LocalDate date;

    private Long courseHid;

    public AttendanceIdClass() {
        this.studentId = null;
        this.date = LocalDate.now();
        this.courseHid = null;
    }

    public AttendanceIdClass(Long studentId, LocalDate date, Long courseHid) {
        this.studentId = studentId;
        this.date = date;
        this.courseHid = courseHid;
    }
}
