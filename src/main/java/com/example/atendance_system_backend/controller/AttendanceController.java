package com.example.atendance_system_backend.controller;

import com.example.atendance_system_backend.course.Course;
import com.example.atendance_system_backend.course.CourseRepository;
import com.example.atendance_system_backend.coursereg.StudentTakesCourse;
import com.example.atendance_system_backend.coursereg.StudentTakesCourseRepository;
import com.example.atendance_system_backend.session.MySession;
import com.example.atendance_system_backend.session.MySessionRepository;
import com.example.atendance_system_backend.student.Student;
import com.example.atendance_system_backend.student.StudentRepository;
import com.example.atendance_system_backend.teacher.Teacher;
import com.example.atendance_system_backend.teacher.TeacherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/attendance")
public class AttendanceController {


    @Autowired
    TeacherRepository teacherDB;

    @Autowired
    StudentRepository studentDB;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    CourseRepository courseDB;

    @Autowired
    StudentTakesCourseRepository courseregDB;

    @GetMapping("/get-students")
    @ResponseBody
    private ResponseEntity<ObjectNode> get_students(@RequestParam Long hid){

        List<StudentTakesCourse> studentids = courseregDB.findAllByCourseHid(hid);

        ObjectNode res_student = mapper.createObjectNode();

        for(StudentTakesCourse x : studentids){
            Optional<Student> s = studentDB.findStudentById(x.getStudentId());
            if(s.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);



            res_student.put(s.get().getId().toString() , s.get().getName() );


        }

        return ResponseEntity.status(HttpStatus.OK).body(res_student);
    }


}
