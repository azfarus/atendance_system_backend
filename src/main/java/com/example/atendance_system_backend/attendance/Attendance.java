package com.example.atendance_system_backend.attendance;


import com.example.atendance_system_backend.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AttendanceIdClass.class)
public class Attendance implements  Comparable<Attendance> {

    @Id
    private Long studentId;

    @Id
    private LocalDate date;


    private String status;

    @Id
    private Long courseHid;

    @Override
    public int compareTo(Attendance other) {
        return this.date.compareTo(other.date);
    }



}
