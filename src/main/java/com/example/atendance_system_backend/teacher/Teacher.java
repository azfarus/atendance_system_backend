package com.example.atendance_system_backend.teacher;

import com.example.atendance_system_backend.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    @Id
    private Long id;

    private String password;

    private String name;

    private  String email;

    @OneToMany(mappedBy = "teacher")
    private Set<Course> course;



}
