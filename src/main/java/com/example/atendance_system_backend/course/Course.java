package com.example.atendance_system_backend.course;

import com.example.atendance_system_backend.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long  hid;

    private String department;

    private Long courseId;

    private Long count;

    private Character section;

    private Long semester;

    private String courseName;
    @ManyToOne
    @JoinColumn(nullable = true)
    private Teacher teacher;


}
