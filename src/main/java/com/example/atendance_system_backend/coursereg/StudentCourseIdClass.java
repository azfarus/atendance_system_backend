package com.example.atendance_system_backend.coursereg;

import java.io.Serializable;

public class StudentCourseIdClass implements Serializable {
    private Long studentId;
    private Long courseHid;

    public StudentCourseIdClass(Long studentId, Long courseHid) {
        this.studentId = studentId;
        this.courseHid = courseHid;
    }
}
