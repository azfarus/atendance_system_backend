package com.example.atendance_system_backend.coursereg;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(StudentCourseIdClass.class)
public class StudentTakesCourse {
    @Id
    private Long studentId;

    @Id
    private Long courseHid;


}
